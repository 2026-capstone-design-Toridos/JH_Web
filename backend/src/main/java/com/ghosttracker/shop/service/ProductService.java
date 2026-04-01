package com.ghosttracker.shop.service;

import com.ghosttracker.shop.dto.product.ProductRequest;
import com.ghosttracker.shop.dto.product.ProductResponse;
import com.ghosttracker.shop.entity.Category;
import com.ghosttracker.shop.entity.Product;
import com.ghosttracker.shop.entity.ProductImage;
import com.ghosttracker.shop.repository.CategoryRepository;
import com.ghosttracker.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private final String uploadDir = "uploads/products";

    public Page<ProductResponse> getProducts(Long categoryId, String keyword,
                                              BigDecimal minPrice, BigDecimal maxPrice,
                                              String sort, int page, int size) {
        Sort sortOrder = switch (sort != null ? sort : "latest") {
            case "price_asc" -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            case "rating" -> Sort.by("id").descending();
            default -> Sort.by("createdAt").descending();
        };

        Pageable pageable = PageRequest.of(page, size, sortOrder);

        Page<Product> products;
        if (keyword != null && !keyword.isBlank()) {
            products = categoryId != null
                    ? productRepository.searchByKeywordAndCategory(keyword, categoryId, pageable)
                    : productRepository.searchByKeyword(keyword, pageable);
        } else if (categoryId != null) {
            products = productRepository.findByStatusAndCategory_Id(Product.ProductStatus.ACTIVE, categoryId, pageable);
        } else if (minPrice != null && maxPrice != null) {
            products = productRepository.findByPriceRange(minPrice, maxPrice, pageable);
        } else {
            products = productRepository.findByStatus(Product.ProductStatus.ACTIVE, pageable);
        }

        return products.map(ProductResponse::from);
    }

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        return ProductResponse.from(product);
    }

    public List<ProductResponse> getNewArrivals() {
        return productRepository.findTop8ByStatusOrderByCreatedAtDesc(Product.ProductStatus.ACTIVE)
                .stream().map(ProductResponse::from).toList();
    }

    public List<ProductResponse> getBestSellers() {
        return productRepository.findTop8ByStatusOrderByIdDesc(Product.ProductStatus.ACTIVE)
                .stream().map(ProductResponse::from).toList();
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest req, MultipartFile mainImage,
                                          List<MultipartFile> detailImages) throws IOException {
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        Product product = Product.builder()
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .discountPrice(req.getDiscountPrice())
                .stock(req.getStock())
                .brand(req.getBrand())
                .category(category)
                .status(req.getStatus() != null
                        ? Product.ProductStatus.valueOf(req.getStatus())
                        : Product.ProductStatus.ACTIVE)
                .build();

        if (mainImage != null && !mainImage.isEmpty()) {
            String filename = saveFile(mainImage);
            product.setMainImage("/uploads/products/" + filename);
        }

        productRepository.save(product);

        if (detailImages != null) {
            int order = 0;
            for (MultipartFile file : detailImages) {
                if (!file.isEmpty()) {
                    String filename = saveFile(file);
                    ProductImage img = ProductImage.builder()
                            .product(product)
                            .imageUrl("/uploads/products/" + filename)
                            .displayOrder(order++)
                            .build();
                    product.getImages().add(img);
                }
            }
            productRepository.save(product);
        }

        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest req, MultipartFile mainImage,
                                          List<MultipartFile> detailImages) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setPrice(req.getPrice());
        product.setDiscountPrice(req.getDiscountPrice());
        product.setStock(req.getStock());
        product.setBrand(req.getBrand());
        product.setCategory(category);
        if (req.getStatus() != null) {
            product.setStatus(Product.ProductStatus.valueOf(req.getStatus()));
        }

        if (mainImage != null && !mainImage.isEmpty()) {
            String filename = saveFile(mainImage);
            product.setMainImage("/uploads/products/" + filename);
        }

        if (detailImages != null && !detailImages.isEmpty()) {
            product.getImages().clear();
            int order = 0;
            for (MultipartFile file : detailImages) {
                if (!file.isEmpty()) {
                    String filename = saveFile(file);
                    ProductImage img = ProductImage.builder()
                            .product(product)
                            .imageUrl("/uploads/products/" + filename)
                            .displayOrder(order++)
                            .build();
                    product.getImages().add(img);
                }
            }
        }

        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        product.setStatus(Product.ProductStatus.INACTIVE);
        productRepository.save(product);
    }

    private String saveFile(MultipartFile file) throws IOException {
        Path dir = Paths.get(uploadDir);
        if (!Files.exists(dir)) Files.createDirectories(dir);
        String ext = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + ext;
        Files.copy(file.getInputStream(), dir.resolve(filename));
        return filename;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return ".jpg";
        return filename.substring(filename.lastIndexOf("."));
    }
}
