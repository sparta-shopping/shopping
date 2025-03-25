package com.example.shopping.domain.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shopping.domain.product.entity.Product;
import com.example.shopping.domain.product.entity.ProductTouchMD;

public interface ProductUserRepository extends JpaRepository<ProductTouchMD, Long> {
	List<ProductTouchMD> findProductUsersByProduct(Product product);
}
