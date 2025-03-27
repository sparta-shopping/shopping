package com.example.shopping.domain.search.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "searchs")
public class Search {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;

    private Integer count = 1;

    public void incrementCount() {
        this.count++;
    }

    public Search(String keyword) {
        this.keyword = keyword;
    }
}
