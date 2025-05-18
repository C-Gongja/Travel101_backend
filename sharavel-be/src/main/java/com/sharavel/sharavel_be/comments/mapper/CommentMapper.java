package com.sharavel.sharavel_be.comments.mapper;

import java.util.stream.Collectors;

import com.sharavel.sharavel_be.comments.dto.CommentsResponseDto;
import com.sharavel.sharavel_be.comments.dto.SingleCommentDto;
import com.sharavel.sharavel_be.comments.entity.Comment;

public class CommentMapper {
	public static CommentsResponseDto toCommentsResponseDto(Comment comment) {
		return new CommentsResponseDto(
				comment.getUid(),
				comment.getContent(),
				comment.getUser().getUsername(),
				comment.getUser().getUuid(),
				comment.getCreatedAt(),
				comment.getReplies().stream()
						.map(CommentMapper::toCommentsResponseDto) // 재귀적 호출
						.collect(Collectors.toList()));
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
