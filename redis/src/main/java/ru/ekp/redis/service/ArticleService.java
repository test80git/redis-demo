package ru.ekp.redis.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ekp.redis.controller.dto.ArticleInfo;
import ru.ekp.redis.entity.Article;
import ru.ekp.redis.entity.Comment;
import ru.ekp.redis.repository.ArticleRepository;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
@Slf4j
public class ArticleService {

    private ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public ArticleInfo getArticle(Long id){
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
    public ArticleInfo getRandomArticle(){
        long count = articleRepository.count();
        log.debug("count = {}", count);
        long articleNum = new Random().nextLong(1, count);
        log.debug("articleNum = {}", articleNum);
        return getArticle(articleNum);
    }

}
