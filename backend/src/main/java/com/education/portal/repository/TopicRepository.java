package com.education.portal.repository;

import com.education.portal.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    @Query("SELECT t FROM Topic t WHERE t.aClass.id = :classId")
    List<Topic> findByAClassId(@Param("classId") Long classId);

    @Query("SELECT COUNT(t) > 0 FROM Topic t WHERE LOWER(t.name) = LOWER(:name) AND t.aClass.id = :classId")
    boolean existsByNameIgnoreCaseAndAClassId(@Param("name") String name, @Param("classId") Long classId);
}
