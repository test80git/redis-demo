package ru.ekp.redis.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    private Long id;

    @Column(name = "text", columnDefinition = "TEXT")
    private String text;

    private Integer score;

    @ManyToOne
    @JoinColumn(name = "article_id")
    @ToString.Exclude
    private Article article;

}
