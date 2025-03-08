package com.dre0059.articleprocessor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "grobid")
public class GrobidProperties {

  private String host;

  public void setHost(String host) {
    this.host = host;
  }

  public String getHost() {
    return host;
  }

}
