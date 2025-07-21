package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.ProductRequest;
import com.ecommerce.product_service.entity.Category;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.event.ProductEvent;
import com.ecommerce.product_service.repository.CategoryRepository;
import com.ecommerce.product_service.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private KafkaTemplate<String, ProductEvent> kafkaTemplate;

    private static final String PRODUCT_TOPIC = "product-events";

    @CacheEvict(value = "products", allEntries = true)
    public Product createProduct(ProductRequest request){

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with ID: " + request.getCategoryId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);
        product.setImageUrls(request.getImageUrls());

        Product savedProduct = productRepository.save(product);

        //Publish a PRODUCT_CREATED event to kafka
        ProductEvent productCreatedEvent = new ProductEvent(
                savedProduct.getId(),
                savedProduct.getName(),
                "PRODUCT_CREATED",
                savedProduct.getPrice(),
                savedProduct.getStock(),
                savedProduct.getCategory().getId(),
                savedProduct.getCategory().getName(),
                savedProduct.getImageUrls()
        );
        kafkaTemplate.send(PRODUCT_TOPIC, savedProduct.getId().toString(), productCreatedEvent);
        System.out.println("Published PRODUCT_CREATED event for product: " + savedProduct.getName());

        return savedProduct;
    }

    @Cacheable(value = "products")
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }
    @Cacheable(value = "product", key = "#id")
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @CacheEvict(value = {"products", "product"}, key = "#id")
    public Product updateProductStock(Long id, Integer newStock){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + id));

        Integer oldStock = product.getStock();
        product.setStock(newStock);
        Product updatedProduct = productRepository.save(product);

        // Publish a STOCK_UPDATED event
        ProductEvent stockUpdatedEvent = new ProductEvent(
                updatedProduct.getId(),
                updatedProduct.getName(),
                "STOCK_UPDATED",
                updatedProduct.getPrice(),
                updatedProduct.getStock(),
                updatedProduct.getCategory().getId(),
                updatedProduct.getCategory().getName(),
                updatedProduct.getImageUrls()
        );

        kafkaTemplate.send(PRODUCT_TOPIC, updatedProduct.getId().toString(), stockUpdatedEvent);
        System.out.println("Published STOCK_UPDATED event for product: " + updatedProduct.getName() + " from " + oldStock + " to " + newStock);

        return updatedProduct;
    }

    @CacheEvict(value = {"products","product"}, key = "#id")
    public void deleteProduct(Long id) {
        if(!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,  "Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
    }


    // Additional methods for updating other product details can be added here
    @CacheEvict(value = {"products", "product"}, key = "#id")
    public Product updateProduct(Long id, ProductRequest request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with ID: " + request.getCategoryId()));

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setStock(request.getStock());
        existingProduct.setCategory(category);
        existingProduct.setImageUrls(request.getImageUrls());

        Product updatedProduct = productRepository.save(existingProduct);

        // Publish a PRODUCT_UPDATED event (consider different event types for specific changes)
        ProductEvent productUpdatedEvent = new ProductEvent(
                updatedProduct.getId(),
                updatedProduct.getName(),
                "PRODUCT_UPDATED",
                updatedProduct.getPrice(),
                updatedProduct.getStock(),
                updatedProduct.getCategory().getId(),
                updatedProduct.getCategory().getName(),
                updatedProduct.getImageUrls()
        );
        kafkaTemplate.send(PRODUCT_TOPIC, updatedProduct.getId().toString(), productUpdatedEvent);
        System.out.println("Published PRODUCT_UPDATED event for product: " + updatedProduct.getName());

        return updatedProduct;
    }

}
