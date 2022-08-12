package com.foton.okla.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foton.okla.model.Comment;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	Optional<Comment> findById(Long id);

	Page<Comment> findByDishId(Long id, Pageable pageable);

}
