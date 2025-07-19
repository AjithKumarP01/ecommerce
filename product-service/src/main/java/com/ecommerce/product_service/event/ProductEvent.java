package com.ecommerce.product_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEvent {
    private Long productId;
    private String productName;
    private String eventType;
    private BigDecimal price;
    private Integer newStock;
    private Long categoryId;
    private String categoryName;
    private List<String> imageUrls;
}
