package com.sharavel.sharavel_be.comments.mapper;

import com.sharavel.sharavel_be.comments.dto.SingleCommentDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sharavel.sharavel_be.comments.dto.CommentsResponseDto;
import com.sharavel.sharavel_be.comments.entity.Comment;
import com.sharavel.sharavel_be.comments.repository.CommentRepository;
import com.sharavel.sharavel_be.likes.repository.LikesRepository;
import com.sharavel.sharavel_be.s3bucket.service.S3ProfileService;
import com.sharavel.sharavel_be.user.entity.Users;

@Component
public class CommentMapper {
	@Autowired
	private LikesRepository likesRepository;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private S3ProfileService s3ProfileService;

	public CommentsResponseDto toCommentsResponseDto(Comment comment, Users user) {
		Comment parentComment = commentRepository.findByUid(comment.getUid()).orElseThrow();
		Long childCount = commentRepository.countByParentAndDeletedFalse(parentComment);
		String picture = comment.getUser().getPicture();
		if (picture == null) {
			picture = s3ProfileService.getS3UserProfileImg(comment.getUser().getUuid());
		}

		boolean isLiked = false;
		if (user != null) {
			isLiked = likesRepository.existsByTargetTypeAndTargetUidAndUser("COMMENT", comment.getUid(), user);
		}
		Long commentLikesCount = likesRepository.countByTargetTypeAndTargetUid("COMMENT", comment.getUid());

		return new CommentsResponseDto(
				comment.getUid(),
				comment.getContent(),
				picture,
				comment.getUser().getUsername(),
				comment.getUser().getUuid(),
				comment.getParent() != null ? comment.getParent().getUid() : null,
				comment.getCreatedAt(),
				isLiked,
				commentLikesCount,
				childCount);
	}

	public CommentsResponseDto toCommentsResponseDtoWithoutReplies(Comment comment, Users user) {
		Comment parentComment = commentRepository.findByUid(comment.getUid()).orElseThrow();
		Long childCount = commentRepository.countByParentAndDeletedFalse(parentComment);
		boolean isLiked = false;
		String picture = comment.getUser().getPicture();
		if (picture == null) {
			picture = s3ProfileService.getS3UserProfileImg(comment.getUser().getUuid());
		}
		if (user != null) {
			isLiked = likesRepository.existsByTargetTypeAndTargetUidAndUser("COMMENT", comment.getUid(), user);
		}
		Long commentLikesCount = likesRepository.countByTargetTypeAndTargetUid("COMMENT", comment.getUid());

		return new CommentsResponseDto(
				comment.getUid(),
				comment.getContent(),
				picture,
				comment.getUser().getUsername(),
				comment.getUser().getUuid(),
				comment.getParent() != null ? comment.getParent().getUid() : null,
				comment.getCreatedAt(),
				isLiked,
				commentLikesCount,
				childCount);
	}

	public SingleCommentDto toSingleCommentDto(Comment comment) {
		String picture = comment.getUser().getPicture();
		if (picture == null) {
			picture = s3ProfileService.getS3UserProfileImg(comment.getUser().getUuid());
		}
		return new SingleCommentDto(
				comment.getUid(),
				comment.getContent(),
				picture,
				comment.getUser().getUsername(),
				comment.getTargetUid(),
				comment.getCreatedAt(),
				comment.getParent() != null ? comment.getParent().getUid() : null);
	}
}
