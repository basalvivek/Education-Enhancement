package com.education.portal.repository;

import com.education.portal.model.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClassRepository extends JpaRepository<Class, Long> {
    List<Class> findByCategoryId(Long categoryId);
    boolean existsByNameIgnoreCaseAndCategoryId(String name, Long categoryId);
}
