package ru.ekp.redis.controller.dto;

public record ArticleInfo (Long id, String title, String text, Double rating) {
}
