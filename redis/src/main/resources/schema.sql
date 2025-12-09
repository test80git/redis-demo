-- Создание таблицы article
CREATE TABLE IF NOT EXISTS article (
                                       id BIGINT PRIMARY KEY,
                                       title VARCHAR(255),
    text TEXT
    );

-- Создание таблицы comment
CREATE TABLE IF NOT EXISTS comment (
                                       id BIGINT PRIMARY KEY,
                                       text VARCHAR(1000),
    score INTEGER NOT NULL CHECK (score >= 1 AND score <= 100),
    article_id BIGINT NOT NULL,
    CONSTRAINT fk_comment_article FOREIGN KEY (article_id)
    REFERENCES article(id) ON DELETE CASCADE
    );
