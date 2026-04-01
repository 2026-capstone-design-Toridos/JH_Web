package com.ghosttracker.shop.repository;

import com.ghosttracker.shop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByStatusAndCategory_Id(Product.ProductStatus status, Long categoryId, Pageable pageable);

    Page<Product> findByStatus(Product.ProductStatus status, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%',:keyword,'%')))")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%',:keyword,'%')) OR " +
           "LOWER(p.brand) LIKE LOWER(CONCAT('%',:keyword,'%'))) AND " +
           "p.category.id = :categoryId")
    Page<Product> searchByKeywordAndCategory(@Param("keyword") String keyword,
                                              @Param("categoryId") Long categoryId,
                                              Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.status = 'ACTIVE' AND " +
           "COALESCE(p.discountPrice, p.price) BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                    @Param("maxPrice") BigDecimal maxPrice,
                                    Pageable pageable);

    List<Product> findTop8ByStatusOrderByCreatedAtDesc(Product.ProductStatus status);

    List<Product> findTop8ByStatusOrderByIdDesc(Product.ProductStatus status);

    @Query("SELECT p FROM Product p LEFT JOIN p.reviews r WHERE p.status = 'ACTIVE' " +
           "GROUP BY p ORDER BY AVG(r.rating) DESC")
    List<Product> findTopRatedProducts(Pageable pageable);
}
