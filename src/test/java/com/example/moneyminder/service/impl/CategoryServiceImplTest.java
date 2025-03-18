package com.example.moneyminder.service.impl;

import com.example.moneyminder.DTOs.CategoryRequest;
import com.example.moneyminder.VMs.CategoryVM;
import com.example.moneyminder.entity.Category;
import com.example.moneyminder.exception.ResourceNotFoundException;
import com.example.moneyminder.mapper.CategoryMapper;
import com.example.moneyminder.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateCategory_Success() {

        CategoryRequest request = new CategoryRequest();
        request.setName("Electronics");
        request.setDescription("Electronic devices and gadgets.");

        Category savedCategory = Category.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic devices and gadgets.")
                .build();

        CategoryVM categoryVM = new CategoryVM();
        categoryVM.setName("Electronics");
        categoryVM.setDescription("Electronic devices and gadgets.");

        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.empty());
        when(categoryMapper.toEntity(request)).thenReturn(savedCategory);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
        when(categoryMapper.toVM(savedCategory)).thenReturn(categoryVM);


        CategoryVM result = categoryService.createCategory(request);


        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        assertEquals("Electronic devices and gadgets.", result.getDescription());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    public void testCreateCategory_Duplicate() {

        CategoryRequest request = new CategoryRequest();
        request.setName("Clothing");
        request.setDescription("Men and Women clothing");

        Category existingCategory = Category.builder()
                .id(2L)
                .name("Clothing")
                .description("Existing description")
                .build();

        when(categoryRepository.findByName("Clothing")).thenReturn(Optional.of(existingCategory));


        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.createCategory(request);
        });
        String expectedMessage = "Category with this name already exists.";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    @Test
    public void testGetCategoryById_Success() {

        Category category = Category.builder()
                .id(1L)
                .name("Books")
                .description("All kinds of books")
                .build();

        CategoryVM categoryVM = new CategoryVM();
        categoryVM.setName("Books");
        categoryVM.setDescription("All kinds of books");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryMapper.toVM(category)).thenReturn(categoryVM);


        CategoryVM result = categoryService.getCategoryById(1L);


        assertNotNull(result);
        assertEquals("Books", result.getName());
    }

    @Test
    public void testGetCategoryById_NotFound() {

        when(categoryRepository.findById(100L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.getCategoryById(100L);
        });
        String expectedMessage = "Category not found with id 100";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}