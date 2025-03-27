package com.example.shopping.domain.search.repository;

import com.example.shopping.domain.product.entity.Product;
import com.example.shopping.domain.search.entity.Search;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface SearchRepository extends JpaRepository<Search, Long>,SearchRepositoryQuery {

    Optional<Search> findByKeyword(String keyword);



}
