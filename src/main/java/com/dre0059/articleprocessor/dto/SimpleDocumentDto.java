package com.dre0059.articleprocessor.dto;

public class SimpleDocumentDto {
  private Long id;
  private String title;

  public String getLink() {
    return "/api/document/"+getId();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
