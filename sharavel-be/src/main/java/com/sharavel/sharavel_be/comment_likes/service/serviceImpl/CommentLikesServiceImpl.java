package com.sharavel.sharavel_be.comment_likes.service.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sharavel.sharavel_be.comment_likes.entity.CommentLikes;
import com.sharavel.sharavel_be.comment_likes.repository.CommentLikesRepository;
import com.sharavel.sharavel_be.comment_likes.service.CommentLikesService;
import com.sharavel.sharavel_be.tripComments.entity.TripComment;
import com.sharavel.sharavel_be.tripComments.repository.TripCommentRepository;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.user.repository.UserRepository;

@Service
public class CommentLikesServiceImpl implements CommentLikesService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private TripCommentRepository commentRepository;
	@Autowired
	private CommentLikesRepository commentLikesRepository;

	private Users getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new IllegalStateException("User not authenticated");
		}
		String email = authentication.getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalStateException("Current user not found"));
	}

	@Override
	public String addLike(String commentUid) {
		Users user = getCurrentUser();
		TripComment comment = commentRepository.findByUid(commentUid)
				.orElseThrow(() -> new IllegalStateException("Current trip not found"));
		CommentLikes commentLike = new CommentLikes();
		commentLike.setUser(user);
		commentLike.setComment(comment);
		commentLike.setLikedAt(LocalDateTime.now());
		commentLikesRepository.save(commentLike);

		return "successfully add like";
	}

	@Override
	public String removeLike(String commentUid) {
		Users user = getCurrentUser();
		TripComment comment = commentRepository.findByUid(commentUid)
				.orElseThrow(() -> new IllegalArgumentException("Comment not found"));

		CommentLikes like = commentLikesRepository.findByUserAndComment(user, comment)
				.orElseThrow(() -> new IllegalStateException("Like not found"));

		commentLikesRepository.delete(like);
		return "successfully remove like";
	}

	@Override
	public int getCommentLikes(String commentUid) {
		TripComment comment = commentRepository.findByUid(commentUid)
				.orElseThrow(() -> new IllegalArgumentException("Comment not found"));
		return commentLikesRepository.countByComment(comment);
	}

}
