package com.example.shopping.domain.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shopping.domain.product.entity.Product;
import com.example.shopping.domain.product.entity.ProductUser;

public interface ProductUserRepository extends JpaRepository<ProductUser, Long> {
	List<ProductUser> findProductUsersByProduct(Product product);
}
