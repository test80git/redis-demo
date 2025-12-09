-- Проверка количества комментариев на статью
SELECT
    a.id,
    a.title,
    COUNT(c.id) as comment_count,
    AVG(c.score) as avg_score
FROM article a
         LEFT JOIN comment c ON a.id = c.article_id
GROUP BY a.id, a.title
ORDER BY a.id;

-- Проверка что все score в пределах 1-100
SELECT
    MIN(score) as min_score,
    MAX(score) as max_score,
    COUNT(*) as total_comments
FROM comment;

select count(*) from article;
select count(*) from comment;