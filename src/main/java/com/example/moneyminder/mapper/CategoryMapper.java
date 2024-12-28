package com.example.moneyminder.mapper;

import com.example.moneyminder.DTOs.CategoryRequest;
import com.example.moneyminder.VMs.CategoryVM;
import com.example.moneyminder.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryVM toVM(Category category);

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryRequest request);
}