package com.dre0059.articleprocessor.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleDocumentDto {
  private Long id;
  private String title;
  private String status;

  public String getLink() {
    return "/api/document/"+getId();
  }
}
