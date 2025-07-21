package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.CategoryRequest;
import com.ecommerce.product_service.entity.Category;
import com.ecommerce.product_service.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @CacheEvict(value = "categories", allEntries = true)
    public Category createCategory(CategoryRequest request){
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return categoryRepository.save(category);
    }

    @Cacheable(value = "categories")
    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    @Cacheable(value = "category", key = "#id")
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @CacheEvict(value = {"categories", "category"}, key = "#id")
    public Category updateCategory(Long id, CategoryRequest request){
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with ID: " + id));
        existingCategory.setName(request.getName());
        existingCategory.setDescription(request.getDescription());
        return categoryRepository.save(existingCategory);
    }

    @CacheEvict(value = {"categories", "category"}, key = "#id")
    public void deleteCategory(Long id){

        if(!categoryRepository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with ID: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
