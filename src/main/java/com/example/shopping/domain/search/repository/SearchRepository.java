package com.example.shopping.domain.search.repository;

import com.example.shopping.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;


public interface SearchRepository extends JpaRepository<Product, Long>,SearchRepositoryQuery {


}
