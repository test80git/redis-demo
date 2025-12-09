package ru.ekp.redis.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ekp.redis.controller.dto.ArticleInfo;
import ru.ekp.redis.entity.Article;
import ru.ekp.redis.service.ArticleService;

import java.util.Map;

@RestController
@AllArgsConstructor
public class ArticleController {

    private ArticleService articleService;

    @GetMapping("/trending")
    public ArticleInfo getRandomArticle(){
        return articleService.getRandomArticle();
    }

    @GetMapping("/articles/{id}")
    public ArticleInfo getArticle(@PathVariable Long id){
//        return articleService.getArticle(id);
        return articleService.getCachedArticle(id);
    }


    @PutMapping("/{id}")
//    @Operation(summary = "Обновить статью")
    public ResponseEntity<Article> updateArticle(
            @PathVariable Long id,
            @RequestBody Map<String, String> updates) {

        String newTitle = updates.get("title");
        String newText = updates.get("text");

        Article updated = articleService.updateArticle(id, newTitle, newText);

        if (updated == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}/cache")
//    @Operation(summary = "Очистить кэш статьи")
    public ResponseEntity<Void> evictCache(@PathVariable Long id) {
        articleService.evictArticleCache(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cache")
//    @Operation(summary = "Очистить весь кэш статей")
    public ResponseEntity<Void> evictAllCache() {
        articleService.evictAllArticleCache();
        return ResponseEntity.ok().build();
    }

}
