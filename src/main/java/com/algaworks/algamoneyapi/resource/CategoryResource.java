package com.algaworks.algamoneyapi.resource;

import com.algaworks.algamoneyapi.event.ResourceCreatedEvent;
import com.algaworks.algamoneyapi.model.Category;
import com.algaworks.algamoneyapi.repository.CategoryRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryResource {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping
    public ResponseEntity<List<Category>> listCategories() {
        List<Category> categories = categoryRepository.findAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("{code}")
    public ResponseEntity<Category> findCategoryById (@PathVariable String code) {
        return categoryRepository.findById(code)
                .map(category -> ResponseEntity.ok(category))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody @Valid Category category, HttpServletResponse response) {
         Category createdCategory = categoryRepository.save(category);

         publisher.publishEvent(new ResourceCreatedEvent(this, response, category.getCategoryId()));

        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    /*    @GetMapping
        public ResponseEntity<?> listCategor() {
            List<Category> categoryList = categoryRepository.findAll();
            return !categoryList.isEmpty() ? ResponseEntity.ok(categoryList) : ResponseEntity.notFound().build();
        }*/
}
