package com.sharavel.sharavel_be.tripComments.mapper;

import com.sharavel.sharavel_be.tripComments.dto.TripCommentsResponseDto;
import com.sharavel.sharavel_be.tripComments.dto.SingleTripCommentDto;
import com.sharavel.sharavel_be.tripComments.entity.TripComment;
import com.sharavel.sharavel_be.tripComments.repository.TripCommentRepository;

public class TripCommentMapper {
	public static TripCommentsResponseDto toCommentsResponseDto(TripComment comment,
			TripCommentRepository tripCommentRepository) {
		TripComment parentComment = tripCommentRepository.findByUid(comment.getUid()).orElseThrow();
		Long childCount = tripCommentRepository.countByParentAndDeletedFalse(parentComment);
		return new TripCommentsResponseDto(
				comment.getUid(),
				comment.getContent(),
				comment.getUser().getUsername(),
				comment.getUser().getUuid(),
				comment.getParent() != null ? comment.getParent().getUid() : null, // Null 체크
				comment.getCreatedAt(),
				childCount);
	}

	public static TripCommentsResponseDto toCommentsResponseDtoWithoutReplies(TripComment comment,
			TripCommentRepository tripCommentRepository) {
		TripComment parentComment = tripCommentRepository.findByUid(comment.getUid()).orElseThrow();
		Long childCount = tripCommentRepository.countByParentAndDeletedFalse(parentComment);
		return new TripCommentsResponseDto(
				comment.getUid(),
				comment.getContent(),
				comment.getUser().getUsername(),
				comment.getUser().getUuid(),
				comment.getParent() != null ? comment.getParent().getUid() : null,
				comment.getCreatedAt(),
				childCount);
	}

	public static SingleTripCommentDto toSingleCommentDto(TripComment comment) {
		return new SingleTripCommentDto(
				comment.getUid(),
				comment.getContent(),
				comment.getUser().getUsername(),
				comment.getUser().getUuid(),
				comment.getCreatedAt(),
				comment.getParent().getUid());
	}
}
