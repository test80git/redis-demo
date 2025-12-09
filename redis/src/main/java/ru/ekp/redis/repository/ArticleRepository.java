package ru.ekp.redis.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ekp.redis.entity.Article;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository <Article, Long> {

    @Query("SELECT a FROM Article a LEFT JOIN FETCH a.comments WHERE a.id = :id")
    Optional<Article> findByIdWithComments(@Param("id") Long id);

    // Или с использованием EntityGraph
    @EntityGraph(attributePaths = {"comments"})
    @Query("SELECT a FROM Article a WHERE a.id = :id")
    Optional<Article> findByIdWithCommentsGraph(@Param("id") Long id);

}
