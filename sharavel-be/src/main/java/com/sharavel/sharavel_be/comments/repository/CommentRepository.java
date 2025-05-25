package com.sharavel.sharavel_be.comments.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sharavel.sharavel_be.comments.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
	// UID 기반 단건 조회
	Optional<Comment> findByUid(String uid);

	// 특정 대상에 달린 루트 댓글 (대댓글 아님)
	List<Comment> findByTargetTypeAndTargetUidAndParentIsNullAndDeletedFalseOrderByCreatedAtAsc(String targetType,
			String targetUid);

	// 특정 댓글의 대댓글
	List<Comment> findByParentAndDeletedFalseOrderByCreatedAtAsc(Comment parent);

	// 대댓글 수
	Long countByParentAndDeletedFalse(Comment parent);

	// 특정 대상의 모든 댓글 (필요 시)
	List<Comment> findByTargetTypeAndTargetUidOrderByCreatedAtAsc(String targetType, String targetUid);

	Long countByTargetTypeAndTargetUidAndDeletedFalse(String targetType, String targetUid);

	boolean existsByTargetTypeAndTargetUid(String targetType, String targetUid);

	boolean existsByUid(String uid);
}
