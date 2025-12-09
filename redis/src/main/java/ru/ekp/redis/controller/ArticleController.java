package ru.ekp.redis.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.ekp.redis.controller.dto.ArticleInfo;
import ru.ekp.redis.service.ArticleService;

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
        return articleService.getArticle(id);
    }

}
