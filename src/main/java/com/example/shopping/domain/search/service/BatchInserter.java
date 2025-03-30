package com.example.shopping.domain.search.service;

import com.example.shopping.domain.product.category.Category;
import com.example.shopping.domain.product.entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.UUID;

@Component
@Profile("local")
public class BatchInserter implements CommandLineRunner {

    private static final String[] PRODUCT_PREFIX = {"신발", "바지", "셔츠", "모자", "아우터", "액세서리"};
    private static final String[] IMAGE_URLS = {
            "https://example.com/image1.jpg",
            "https://example.com/image2.jpg",
            "https://example.com/image3.jpg"
    };

    @PersistenceContext
    private EntityManager em;

    @Transactional
    @Override
    public void run(String... args) {
        final int BATCH_SIZE = 500;
        Random random = new Random();


        for (int i = 0; i < 50_000; i++) {
            // 랜덤 데이터 생성
            Category category = Category.values()[random.nextInt(Category.values().length)];
            String productName = generateProductName(category, i);
            int price = (random.nextInt(50) + 1) * 1000; // 1,000 ~ 50,000원
            int stock = random.nextInt(200); // 0 ~ 199개
            String imageUrl = IMAGE_URLS[random.nextInt(IMAGE_URLS.length)];

            Product product = Product.create(
                    productName,
                    category,
                    price,
                    stock,
                    imageUrl
            );
            em.persist(product);

            // 배치 플러시
            if (i % BATCH_SIZE == 0 && i > 0) {
                em.flush();
                em.clear();
            }
        }
        em.flush();
    }
    private String generateProductName(Category category, int index) {
        return String.format("%s_%d_%s",
                category.name(),
                index,
                UUID.randomUUID().toString().substring(0, 6)
        );
    }
}
