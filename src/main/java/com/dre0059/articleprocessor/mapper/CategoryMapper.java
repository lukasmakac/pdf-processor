package com.dre0059.articleprocessor.mapper;

import com.dre0059.articleprocessor.dto.CategoryDto;
import com.dre0059.articleprocessor.model.Category;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

  CategoryDto toCategoryDto(Category entity);

  Category toCategory(CategoryDto categoryDto);

  List<CategoryDto> toCategoryDtoList(List<Category> entities);

}
