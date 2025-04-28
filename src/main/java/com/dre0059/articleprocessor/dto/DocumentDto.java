package com.dre0059.articleprocessor.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DocumentDto {
  private Long id;
  private String title;
  private Integer publicationYear;
  private String doi;
  private String abstractText;
  private String status;
  private String publisher;
  private String target;

  private List<TagDto> tags;
}
