package com.sharavel.sharavel_be.comments.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sharavel.sharavel_be.comments.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
	Optional<Comment> findByUid(String uid);
}
