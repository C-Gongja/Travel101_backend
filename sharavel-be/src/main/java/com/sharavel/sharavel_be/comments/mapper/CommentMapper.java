package com.sharavel.sharavel_be.comments.mapper;

import com.sharavel.sharavel_be.comments.dto.SingleCommentDto;
import com.sharavel.sharavel_be.comments.dto.CommentsResponseDto;
import com.sharavel.sharavel_be.comments.entity.Comment;
import com.sharavel.sharavel_be.comments.repository.CommentRepository;

public class CommentMapper {
	public static CommentsResponseDto toCommentsResponseDto(Comment comment,
			CommentRepository CommentRepository) {
		Comment parentComment = CommentRepository.findByUid(comment.getUid()).orElseThrow();
		Long childCount = CommentRepository.countByParentAndDeletedFalse(parentComment);
		return new CommentsResponseDto(
				comment.getUid(),
				comment.getContent(),
				comment.getUser().getUsername(),
				comment.getUser().getUuid(),
				comment.getParent() != null ? comment.getParent().getUid() : null, // Null 체크
				comment.getCreatedAt(),
				childCount);
	}

	public static CommentsResponseDto toCommentsResponseDtoWithoutReplies(Comment comment,
			CommentRepository CommentRepository) {
		Comment parentComment = CommentRepository.findByUid(comment.getUid()).orElseThrow();
		Long childCount = CommentRepository.countByParentAndDeletedFalse(parentComment);
		return new CommentsResponseDto(
				comment.getUid(),
				comment.getContent(),
				comment.getUser().getUsername(),
				comment.getUser().getUuid(),
				comment.getParent() != null ? comment.getParent().getUid() : null,
				comment.getCreatedAt(),
				childCount);
	}

	public static SingleCommentDto toSingleCommentDto(Comment comment) {
		return new SingleCommentDto(
				comment.getUid(),
				comment.getContent(),
				comment.getUser().getUsername(),
				comment.getUser().getUuid(),
				comment.getCreatedAt(),
				comment.getParent().getUid());
	}
}
