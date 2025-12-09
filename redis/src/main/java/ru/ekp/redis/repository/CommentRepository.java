package ru.ekp.redis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ekp.redis.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
