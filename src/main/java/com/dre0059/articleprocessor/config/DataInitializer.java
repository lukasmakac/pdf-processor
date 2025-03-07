package com.dre0059.articleprocessor.config;

import com.dre0059.articleprocessor.model.Category;
import com.dre0059.articleprocessor.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    // automaticky sa to vykoná, nie je potrebné to znovu volať
    @Bean
    public CommandLineRunner init(CategoryRepository categoryRepository) {
        return args -> {
            if(categoryRepository.count() == 0) {
                List<Category> categories = List.of(
                        new Category("1.4", "Chemical sciences"),
                        new Category("1.5", "Earth and related environmental sciences"),
                        new Category("1.6", "Biological sciences"),
                        new Category("1.7", "Other natural sciences"),
                        new Category("2.1", "Civil engineering"),
                        new Category("2.2", "Electrical engineering, electronic engineering, information engineering"),
                        new Category("2.3", "Mechanical engineering"),
                        new Category("2.4", "Chemical engineering"),
                        new Category("2.5", "Materials engineering"),
                        new Category("2.6", "Medical engineering"),
                        new Category("2.7", "Environmental engineering"),
                        new Category("2.8", "Environmental biotechnology"),
                        new Category("2.9", "Industrial biotechnology"),
                        new Category("2.10", "Nano-technology"),
                        new Category("2.11", "Other engineering and technologies"),
                        new Category("3.2", "Clinical medicine"),
                        new Category("3.3", "Health sciences"),
                        new Category("4.1", "Agriculture, forestry, and fisheries"),
                        new Category("4.2", "Animal and dairy science"),
                        new Category("4.3", "Veterinary science"),
                        new Category("4.5", "Other agricultural sciences"),
                        new Category("5.1", "Psychology and cognitive sciences"),
                        new Category("5.2", "Economics and business"),
                        new Category("5.3", "Education"),
                        new Category("5.4", "Sociology"),
                        new Category("5.5", "Law"),
                        new Category("5.6", "Political science"),
                        new Category("5.7", "Social and economic geography"),
                        new Category("5.8", "Media and communication"),
                        new Category("5.9", "Other social sciences"),
                        new Category("6.1", "History and archaeology"),
                        new Category("6.2", "Languages and literature"),
                        new Category("6.4", "Arts"),
                        new Category("6.5", "Other Humanities and the Arts")
                );
                categoryRepository.saveAll(categories);
            }
        };
    }
}
