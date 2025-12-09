package ru.ekp.redis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ekp.redis.controller.dto.ArticleInfo;
import ru.ekp.redis.entity.Article;
import ru.ekp.redis.entity.Comment;
import ru.ekp.redis.repository.ArticleRepository;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
//@AllArgsConstructor
@Slf4j
public class ArticleService {

    private ArticleRepository articleRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String ARTICLE_CACHE_KEY = "article:%d";
    private long ttlSeconds;

    @Autowired
    public ArticleService(
            ArticleRepository articleRepository,
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            @Value("${cache.redis.ttl:100}") long ttlSeconds) {
        this.articleRepository = articleRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.ttlSeconds = ttlSeconds;
    }


    @PostConstruct
    public void init() {
        System.out.println(articleRepository.count());
    }

    @Transactional(readOnly = true)
    public ArticleInfo getArticle(Long id) {
        Optional<Article> articleById = articleRepository.findByIdWithComments(id);
        log.debug("articleById = {}", articleById.get().getId());
        return articleById.map(article -> new ArticleInfo(
                article.getId(),
                article.getTitle(),
                article.getText(),
                article.getComments().stream()
                        .mapToInt(Comment::getScore)
                        .average().orElse(0)
        )).orElse(null);
    }

    @Transactional(readOnly = true)
    public ArticleInfo getRandomArticle() {
        long count = articleRepository.count();
        log.debug("count = {}", count);
        long articleNum = new Random().nextLong(1, count);
        log.debug("articleNum = {}", articleNum);
//        return getArticle(articleNum);
        return getCachedArticle(articleNum);
    }

    @Transactional
    public ArticleInfo getCachedArticle(Long id) {
        String cacheKey = String.format(ARTICLE_CACHE_KEY, id);

        try {
            // 1. Пытаемся получить из кэша
            Object cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                try {
                    // Пытаемся десериализовать разными способами
                    ArticleInfo articleInfo = convertToArticleInfo(cached);
                    if (articleInfo != null) {
                        log.debug("Статья {} найдена в кэше Redis", id);
                        return articleInfo;
                    }
                } catch (Exception e) {
                    log.warn("Ошибка десериализации кэша для статьи {}, очищаем кэш", id, e);
                    redisTemplate.delete(cacheKey);
                }
            }

            log.debug("Статья {} не найдена в кэше, загружаем из БД", id);

            // 2. Если нет в кэше - загружаем из БД
            ArticleInfo article = getArticle(id);

            if (article == null) {
                log.warn("Статья {} не найдена в БД", id);
                return null;
            }

            // 3. Сохраняем в кэш
            redisTemplate.opsForValue().set(
                    cacheKey,
                    article,
                    ttlSeconds,
                    TimeUnit.SECONDS
            );

            log.debug("Статья {} сохранена в кэш Redis на {} секунд",
                    id, ttlSeconds);

            return article;

        } catch (Exception e) {
            log.error("Ошибка при работе с кэшем Redis для статьи {}", id, e);
            // Fallback: возвращаем из БД без кэширования
            return getArticle(id);
        }
    }

    /**
     * Очистка кэша статьи
     */
    public void evictArticleCache(Long id) {
        String cacheKey = String.format(ARTICLE_CACHE_KEY, id);
        redisTemplate.delete(cacheKey);
        log.debug("Кэш статьи {} очищен", id);
    }

    /**
     * Очистка всего кэша статей
     */
    public void evictAllArticleCache() {
        String pattern = "article:*";
        redisTemplate.delete(redisTemplate.keys(pattern));
        log.debug("Весь кэш статей очищен");
    }

    /**
     * Обновление статьи с инвалидацией кэша
     */
    @Transactional
    public Article updateArticle(Long id, String newTitle, String newText) {
        Optional<Article> articleOpt = articleRepository.findById(id);

        if (articleOpt.isEmpty()) {
            return null;
        }

        Article article = articleOpt.get();
        article.setTitle(newTitle);
        article.setText(newText);

        // Инвалидируем кэш
        evictArticleCache(id);

        return articleRepository.save(article);
    }

    private ArticleInfo convertToArticleInfo(Object cached) throws Exception {
        if (cached instanceof ArticleInfo) {
            return (ArticleInfo) cached;
        }

        if (cached instanceof String) {
            // Если это JSON строка
            return objectMapper.readValue((String) cached, ArticleInfo.class);
        }

        // Если это LinkedHashMap (старые данные)
        return objectMapper.convertValue(cached, ArticleInfo.class);
    }

}
