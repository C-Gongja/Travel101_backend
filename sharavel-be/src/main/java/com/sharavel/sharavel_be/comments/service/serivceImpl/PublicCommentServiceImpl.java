package com.sharavel.sharavel_be.comments.service.serivceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.sharavel.sharavel_be.comments.dto.CommentsResponseDto;
import com.sharavel.sharavel_be.comments.entity.Comment;
import com.sharavel.sharavel_be.comments.mapper.CommentMapper;
import com.sharavel.sharavel_be.comments.repository.CommentRepository;
import com.sharavel.sharavel_be.comments.service.PublicCommentService;
import com.sharavel.sharavel_be.likes.repository.LikesRepository;
import com.sharavel.sharavel_be.s3bucket.service.S3ProfileService;
import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.user.repository.UserRepository;

@Service
public class PublicCommentServiceImpl implements PublicCommentService {
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private LikesRepository likesRepository;

	@Autowired
	private S3ProfileService s3ProfileService;

	@Autowired
	private CommentMapper commentMapper;

	private Users getCurrentUserNullable() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated() ||
				"anonymousUser".equals(authentication.getPrincipal())) {
			return null;
		}
		String email = authentication.getName();
		return userRepository.findByEmail(email).orElse(null);
	}

	private CommentsResponseDto buildCommentReponseDto(Comment comment, Users user) {
		Comment parentComment = commentRepository.findByUid(comment.getUid()).orElseThrow();
		Long childCount = commentRepository.countByParentAndDeletedFalse(parentComment);
		boolean isLiked = false;
		if (user != null) {
			isLiked = likesRepository.existsByTargetTypeAndTargetUidAndUser("COMMENT", comment.getUid(), user);
		}
		Long commentLikesCount = likesRepository.countByTargetTypeAndTargetUid("COMMENT", comment.getUid());

		String profilePic = comment.getUser().getPicture();
		if (profilePic != null && profilePic.startsWith("sharavel-profile:")) {
			String s3Key = profilePic.replace("sharavel-profile:", "");
			profilePic = s3ProfileService.generatePresignedUrl(s3Key, 604800).toString();
		}

		return commentMapper.toCommentsResponseDtoWithoutReplies(
				comment,
				profilePic,
				isLiked,
				commentLikesCount,
				childCount);
	}

	@Override
	public ResponseEntity<?> getRootComments(String targetType, String targetUid) {
		// getCurrentUserNullable for public (should have better solution)
		Users currentUser = getCurrentUserNullable();
		List<Comment> rootComments = commentRepository
				.findByTargetTypeAndTargetUidAndParentIsNullAndDeletedFalseOrderByCreatedAtDesc(targetType, targetUid);

		List<CommentsResponseDto> response = rootComments.stream()
				.map(comment -> buildCommentReponseDto(comment, currentUser))
				.collect(Collectors.toList());

		return ResponseEntity.ok(response);
	}

	@Override
	public ResponseEntity<?> getReplies(String parentUid) {
		Users currentUser = getCurrentUserNullable();
		Comment parent = commentRepository.findByUid(parentUid)
				.orElseThrow(() -> new RuntimeException("Parent comment not found"));

		List<Comment> replies = commentRepository
				.findByParentAndDeletedFalseOrderByCreatedAtDesc(parent);

		List<CommentsResponseDto> response = replies.stream()
				.map(comment -> buildCommentReponseDto(comment, currentUser))
				.collect(Collectors.toList());

		return ResponseEntity.ok(response);
	}
}
