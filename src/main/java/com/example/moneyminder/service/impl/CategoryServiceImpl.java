package com.example.moneyminder.service.impl;

import com.example.moneyminder.DTOs.CategoryRequest;
import com.example.moneyminder.VMs.CategoryVM;
import com.example.moneyminder.entity.Category;
import com.example.moneyminder.exception.ResourceNotFoundException;
import com.example.moneyminder.mapper.CategoryMapper;
import com.example.moneyminder.repository.CategoryRepository;
import com.example.moneyminder.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryVM createCategory(CategoryRequest request) {
        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Category with this name already exists.");
        }
        Category category = categoryMapper.toEntity(request);
        category = categoryRepository.save(category);
        return categoryMapper.toVM(category);
    }

    @Override
    public CategoryVM updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        category = categoryRepository.save(category);
        return categoryMapper.toVM(category);
    }

    @Override
    public CategoryVM getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
        return categoryMapper.toVM(category);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id " + id);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryVM> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toVM)
                .collect(Collectors.toList());
    }
}
