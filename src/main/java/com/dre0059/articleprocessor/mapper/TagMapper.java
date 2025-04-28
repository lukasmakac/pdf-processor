package com.dre0059.articleprocessor.mapper;

import com.dre0059.articleprocessor.dto.TagDto;
import com.dre0059.articleprocessor.model.Tag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {
    TagDto toTagDto(Tag tag);

    Tag toTag(TagDto tagDto);

    List<TagDto> toTagDtoList(List<Tag> tags);
    List<Tag> toTagList(List<TagDto> tagDtos);

}
