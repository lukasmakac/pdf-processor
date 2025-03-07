package com.dre0059.articleprocessor.mapper;

import com.dre0059.articleprocessor.dto.DocumentDto;
import com.dre0059.articleprocessor.dto.SimpleDocumentDto;
import com.dre0059.articleprocessor.model.Dokument;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

  DocumentDto toDocumentDto(Dokument entity);
  SimpleDocumentDto toSimpleDocument(Dokument entity);

  List<SimpleDocumentDto> toSimpleDocumentList(List<Dokument> entities);
}
