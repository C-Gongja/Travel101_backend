package com.sharavel.sharavel_be.comments.mapper;

import com.sharavel.sharavel_be.comments.dto.SingleCommentDto;

import org.springframework.stereotype.Component;

import com.sharavel.sharavel_be.comments.dto.CommentsResponseDto;
import com.sharavel.sharavel_be.comments.entity.Comment;

@Component
public class CommentMapper {

	public CommentsResponseDto toCommentsResponseDtoWithoutReplies(
			Comment comment,
			String profilePic,
			boolean isLiked,
			Long commentLikesCount,
			Long childCount) {
		return new CommentsResponseDto(
				comment.getUid(),
				comment.getContent(),
				profilePic,
				comment.getUser().getUsername(),
				comment.getUser().getUuid(),
				comment.getParent() != null ? comment.getParent().getUid() : null,
				comment.getCreatedAt(),
				isLiked,
				commentLikesCount,
				childCount);
	}

	public SingleCommentDto toSingleCommentDto(Comment comment, String profilePic) {
		return new SingleCommentDto(
				comment.getUid(),
				comment.getContent(),
				profilePic,
				comment.getUser().getUsername(),
				comment.getTargetUid(),
				comment.getCreatedAt(),
				comment.getParent() != null ? comment.getParent().getUid() : null);
	}
}
