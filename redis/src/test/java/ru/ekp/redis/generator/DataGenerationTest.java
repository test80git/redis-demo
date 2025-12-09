package ru.ekp.redis.generator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.ekp.redis.entity.Article;
import ru.ekp.redis.entity.Comment;
import ru.ekp.redis.repository.ArticleRepository;
import ru.ekp.redis.repository.CommentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
@Commit
public class DataGenerationTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    private final Random random = new Random();
    private final AtomicLong articleIdCounter = new AtomicLong(56);
    private final AtomicLong commentIdCounter = new AtomicLong(282);

    private static final String[] ARTICLE_TEXTS = {
            "Technology advances quickly.", // Короткие тексты
            "New research shows progress.",
            "Future breakthroughs expected.",
            "Experts agree on trends.",
            "Development improves efficiency.",
            "Growth in this sector.",
            "Potential is enormous.",
            "Active development ongoing.",
            "Companies invest heavily.",
            "Research area popular now."
    };

    private static final String[] ARTICLE_TITLES = {
            "AI", "Cloud", "Blockchain",
            "IoT", "5G", "Quantum",
            "Security", "Big Data", "ML",
            "Robotics", "Nano", "Biotech",
            "VR", "AR", "Smart Cities",
            "Autonomous", "Digital", "FinTech",
            "Telemed", "Green", "Space", "Edge"
    };

    private static final String[] COMMENT_TEXTS = {
            "Excellent article!", "Very informative", "Interesting perspective on the problem",
            "Thanks for the useful information", "Would like more practical examples",
            "Don't quite agree with the author", "Too much filler text", "Outdated information",
            "Great overview of current state", "Looking forward to continuation",
            "There are inaccuracies in facts", "Too superficial", "Deep problem analysis",
            "Useful recommendations", "Could have covered in more detail",
            "Interesting prospects", "Already outdated", "Relevant topic",
            "Well-structured material", "Lack specific examples"
    };

    @Test
    @DisplayName("Генерация и сохранение 100 статей с комментариями")
    void generateAndSave100Articles() {
        // Генерируем данные
        int articleCount = 100;
        int minComments = 30;
        int maxComments = 100;

        long startTime = System.currentTimeMillis();

        List<Article> articles = generateArticles(articleCount, minComments, maxComments);

        // Сохраняем в БД
        articleRepository.saveAll(articles);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Проверяем результат
        long savedArticleCount = articleRepository.count();
        long savedCommentCount = commentRepository.count();

        // Проверяем, что данные сохранились
//        assertEquals(articleCount, savedArticleCount);

        // Проверяем диапазон комментариев
        int expectedMinComments = articleCount * minComments;
        int expectedMaxComments = articleCount * maxComments;
        assertTrue(savedCommentCount >= expectedMinComments,
                "Минимум " + expectedMinComments + " комментариев");
//        assertTrue(savedCommentCount <= expectedMaxComments,
//                "Максимум " + expectedMaxComments + " комментариев");

        // Выводим статистику
        printStatistics();
    }

//    @Test
//    @DisplayName("Генерация 55 статей как в требовании")
//    void generate55ArticlesAsRequired() {
//        // Генерируем 55 статей (как в исходном требовании)
//        int articleCount = 55;
//        int minComments = 3;
//        int maxComments = 7;
//
//        List<Article> articles = generateArticles(articleCount, minComments, maxComments);
//
//        // Сохраняем
//        articleRepository.saveAll(articles);
//
//        // Проверяем
//        assertEquals(55, articleRepository.count());
//
//        // Статистика
//        long commentCount = commentRepository.count();
//        double avgComments = (double) commentCount / articleCount;
//
//        assertTrue(avgComments >= minComments && avgComments <= maxComments);
//    }
//
//    @Test
//    @DisplayName("Генерация 1000 статей для нагрузочного тестирования")
//    void generate1000ArticlesForLoadTest() {
//        // Генерируем большой объем данных
//        int articleCount = 1000;
//        int minComments = 5;
//        int maxComments = 15;
//
//        long startTime = System.currentTimeMillis();
//
//        List<Article> articles = generateArticles(articleCount, minComments, maxComments);
//
//        // Сохраняем пачками для оптимизации
//        int batchSize = 100;
//        for (int i = 0; i < articles.size(); i += batchSize) {
//            int end = Math.min(articles.size(), i + batchSize);
//            List<Article> batch = articles.subList(i, end);
//            articleRepository.saveAll(batch);
//        }
//
//        long endTime = System.currentTimeMillis();
//        long duration = endTime - startTime;
//
//        // Статистика
//        long commentCount = commentRepository.count();
//        double articlesPerSecond = (articleCount * 1000.0) / duration;
//        double commentsPerSecond = (commentCount * 1000.0) / duration;
//
//        assertTrue(duration < 30000, "Генерация 1000 статей должна занимать < 30 секунд");
//    }
//
//    @Test
//    @DisplayName("Генерация данных с разным количеством комментариев")
//    void generateArticlesWithDifferentCommentCounts() {
//        // Тестируем разные сценарии
//        int[][] testCases = {
//                {10, 0, 0},    // 10 статей без комментариев
//                {10, 1, 1},    // ровно по 1 комментарию
//                {10, 5, 5},    // ровно по 5 комментариев
//                {10, 1, 10},   // от 1 до 10 комментариев
//                {20, 3, 7}     // как в оригинальном примере
//        };
//
//        for (int[] testCase : testCases) {
//            int articles = testCase[0];
//            int minComments = testCase[1];
//            int maxComments = testCase[2];
//
//            // Очищаем БД перед каждым тестом
//            articleRepository.deleteAll();
//
//            // Генерируем и сохраняем
//            List<Article> generated = generateArticles(articles, minComments, maxComments);
//            articleRepository.saveAll(generated);
//
//            // Проверяем
//            assertEquals(articles, articleRepository.count());
//
//            long commentCount = commentRepository.count();
//            if (minComments == maxComments) {
//                assertEquals(articles * minComments, commentCount);
//            } else {
//                assertTrue(commentCount >= articles * minComments);
//                assertTrue(commentCount <= articles * maxComments);
//            }
//        }
//    }
//
//    @Test
//    @DisplayName("Генерация и проверка качества данных")
//    void generateAndValidateDataQuality() {
//        // Генерируем тестовые данные
//        List<Article> articles = generateArticles(50, 2, 6);
//
//        // Сохраняем
//        articleRepository.saveAll(articles);
//
//        // Проверяем качество данных
//        List<Article> savedArticles = articleRepository.findAll();
//
//        for (Article article : savedArticles) {
//            // Проверка статьи
//            assertNotNull(article.getId());
//            assertNotNull(article.getTitle());
//            assertNotNull(article.getText());
//            assertFalse(article.getTitle().isEmpty());
//            assertFalse(article.getText().isEmpty());
//
//            // Проверка комментариев
//            assertNotNull(article.getComments());
//            assertFalse(article.getComments().isEmpty());
//
//            for (Comment comment : article.getComments()) {
//                assertNotNull(comment.getId());
//                assertNotNull(comment.getText());
//                assertNotNull(comment.getScore());
//                assertSame(article, comment.getArticle());
//
//                // Проверка диапазона score
//                assertTrue(comment.getScore() >= 1, "Score должен быть >= 1");
//                assertTrue(comment.getScore() <= 100, "Score должен быть <= 100");
//
//                // Проверка, что текст комментария содержит score
//                assertTrue(comment.getText().contains("оценка:"));
//            }
//        }
//
//    }

    // ========== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ==========

    /**
     * Генерирует список статей с комментариями
     */
    private List<Article> generateArticles(int articleCount, int minComments, int maxComments) {
        List<Article> articles = new ArrayList<>();

        for (int i = 0; i < articleCount; i++) {
            Article article = generateArticle();
            int commentCount = minComments + random.nextInt(maxComments - minComments + 1);
            generateCommentsForArticle(article, commentCount);
            articles.add(article);
        }

        return articles;
    }

    /**
     * Генерирует одну статью
     */
    private Article generateArticle() {

        long id = articleIdCounter.getAndIncrement();
        String title = ARTICLE_TITLES[random.nextInt(ARTICLE_TITLES.length)] +
                       " # " + (random.nextInt(100) + 1);
        String text = ARTICLE_TEXTS[random.nextInt(ARTICLE_TEXTS.length)] +
                      " Technology improves life.";

        return Article.builder()
                .id(id)
                .title(title)
                .text(text)
                .build();
    }

    /**
     * Генерирует комментарии для статьи
     */
    private void generateCommentsForArticle(Article article, int commentCount) {
        List<Comment> comments = new ArrayList<>();

        for (int i = 0; i < commentCount; i++) {
            Comment comment = generateComment(article);
            comments.add(comment);
        }

        article.setComments(comments);
    }

    /**
     * Генерирует один комментарий
     */
    private Comment generateComment(Article article) {
        long id = commentIdCounter.getAndIncrement();
        String text = COMMENT_TEXTS[random.nextInt(COMMENT_TEXTS.length)];
        int score = 1 + random.nextInt(100); // от 1 до 100

        return Comment.builder()
                .id(id)
                .text(text + " (оценка: " + score + ")")
                .score(score)
                .article(article)
                .build();
    }

    /**
     * Выводит статистику по сохраненным данным
     */
    private void printStatistics() {
        List<Article> allArticles = articleRepository.findAll();

        if (allArticles.isEmpty()) {
            return;
        }

        long totalComments = allArticles.stream()
                .mapToInt(a -> a.getComments().size())
                .sum();

        double avgComments = (double) totalComments / allArticles.size();

        // Средний score
        double avgScore = allArticles.stream()
                .flatMap(a -> a.getComments().stream())
                .mapToInt(Comment::getScore)
                .average()
                .orElse(0.0);

        // Распределение score
        long scoreBelow50 = allArticles.stream()
                .flatMap(a -> a.getComments().stream())
                .filter(c -> c.getScore() < 50)
                .count();

        long scoreAbove80 = allArticles.stream()
                .flatMap(a -> a.getComments().stream())
                .filter(c -> c.getScore() > 80)
                .count();
    }

}
