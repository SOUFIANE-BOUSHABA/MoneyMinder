package com.example.moneyminder.service;

import com.example.moneyminder.DTOs.CategoryRequest;
import com.example.moneyminder.VMs.CategoryVM;

import java.util.List;

public interface CategoryService {
    CategoryVM createCategory(CategoryRequest request);

    CategoryVM updateCategory(Long id, CategoryRequest request);

    CategoryVM getCategoryById(Long id);

    void deleteCategory(Long id);

    List<CategoryVM> getAllCategories();
}
