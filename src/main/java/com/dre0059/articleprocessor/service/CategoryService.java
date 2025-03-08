package com.dre0059.articleprocessor.service;

import com.dre0059.articleprocessor.dto.CategoryDto;
import com.dre0059.articleprocessor.mapper.CategoryMapper;
import com.dre0059.articleprocessor.repository.CategoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

  private final CategoryMapper categoryMapper;
  private final CategoryRepository categoryRepository;

  public CategoryService(CategoryMapper categoryMapper, CategoryRepository categoryRepository) {
    this.categoryMapper = categoryMapper;
    this.categoryRepository = categoryRepository;
  }

  public CategoryDto getCategory(String id) {
    return categoryMapper.toCategoryDto(categoryRepository.findById(id).orElse(null));
  }

  public List<CategoryDto> getAll() {
    return categoryMapper.toCategoryDtoList(categoryRepository.findAll());
  }
}
