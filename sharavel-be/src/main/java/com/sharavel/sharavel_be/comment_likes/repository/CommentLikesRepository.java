package com.sharavel.sharavel_be.comment_likes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sharavel.sharavel_be.comment_likes.entity.CommentLikes;
import com.sharavel.sharavel_be.comments.entity.Comment;
import com.sharavel.sharavel_be.user.entity.Users;

@Repository
public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long> {
	boolean existsByUserAndComment(Users user, Comment comment);

	Optional<CommentLikes> findByUserAndComment(Users user, Comment comment);

	int countByComment(Comment trip);
}
