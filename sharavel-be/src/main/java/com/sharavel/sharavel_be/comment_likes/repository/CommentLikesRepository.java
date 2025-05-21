package com.sharavel.sharavel_be.comment_likes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sharavel.sharavel_be.comment_likes.entity.CommentLikes;
import com.sharavel.sharavel_be.tripComments.entity.TripComment;
import com.sharavel.sharavel_be.user.entity.Users;

@Repository
public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long> {
	boolean existsByUserAndComment(Users user, TripComment comment);

	Optional<CommentLikes> findByUserAndComment(Users user, TripComment comment);

	int countByComment(TripComment trip);
}
