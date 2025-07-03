package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByLevel(Integer level);

    @EntityGraph(attributePaths = {"parent"})
    @Query("select c from Category c where c.id = :id")
    Optional<Category> findByIdWithParent(@Param("id") Long id);
}
