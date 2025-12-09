CREATE TABLE article
(
    id    BIGINT NOT NULL,
    title VARCHAR(255),
    text  VARCHAR(255),
    CONSTRAINT pk_article PRIMARY KEY (id)
);

CREATE TABLE comment
(
    id         BIGINT NOT NULL,
    text       VARCHAR(255),
    score      INTEGER,
    article_id BIGINT,
    CONSTRAINT pk_comment PRIMARY KEY (id)
);

ALTER TABLE comment
    ADD CONSTRAINT FK_COMMENT_ON_ARTICLE FOREIGN KEY (article_id) REFERENCES article (id);