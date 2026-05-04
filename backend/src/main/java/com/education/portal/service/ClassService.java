package com.education.portal.service;

import com.education.portal.dto.ClassRequest;
import com.education.portal.dto.ClassResponse;
import com.education.portal.model.Category;
import com.education.portal.model.Class;
import com.education.portal.repository.CategoryRepository;
import com.education.portal.repository.ClassRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassService {

    private final ClassRepository classRepository;
    private final CategoryRepository categoryRepository;

    public List<ClassResponse> getByCategoryId(Long categoryId) {
        return classRepository.findByCategoryId(categoryId).stream()
                .map(ClassResponse::from)
                .toList();
    }

    public ClassResponse getById(Long id) {
        return ClassResponse.from(findOrThrow(id));
    }

    public ClassResponse create(ClassRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + request.getCategoryId()));

        if (classRepository.existsByNameIgnoreCaseAndCategoryId(request.getName(), request.getCategoryId())) {
            throw new IllegalArgumentException("Class already exists in this category: " + request.getName());
        }

        Class aClass = Class.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .build();
        return ClassResponse.from(classRepository.save(aClass));
    }

    public ClassResponse update(Long id, ClassRequest request) {
        Class aClass = findOrThrow(id);
        aClass.setName(request.getName());
        aClass.setDescription(request.getDescription());
        return ClassResponse.from(classRepository.save(aClass));
    }

    public void delete(Long id) {
        findOrThrow(id);
        classRepository.deleteById(id);
    }

    private Class findOrThrow(Long id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Class not found: " + id));
    }
}
