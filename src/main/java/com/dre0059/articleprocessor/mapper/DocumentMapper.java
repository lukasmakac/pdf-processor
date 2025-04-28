package com.dre0059.articleprocessor.mapper;

import com.dre0059.articleprocessor.dto.DocumentContentDto;
import com.dre0059.articleprocessor.dto.DocumentDto;
import com.dre0059.articleprocessor.dto.SimpleDocumentDto;
import com.dre0059.articleprocessor.model.Dokument;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {TagMapper.class})
public interface DocumentMapper {

  //@Mapping(target = "publication_year", source = "year")
  DocumentDto toDocumentDto(Dokument entity);

  DocumentContentDto toDocumentContentDto(Dokument entity);

  SimpleDocumentDto toSimpleDocumentDto(Dokument entity);

  List<SimpleDocumentDto> toSimpleDocumentList(List<Dokument> entities);

  default String toString(byte[] bytes) {
    if (bytes == null) {
      return null;
    } else {
      return new String(bytes);
    }
  }



}
