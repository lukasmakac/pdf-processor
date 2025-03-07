package com.dre0059.articleprocessor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "categories")

public class Category {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category() {}

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
