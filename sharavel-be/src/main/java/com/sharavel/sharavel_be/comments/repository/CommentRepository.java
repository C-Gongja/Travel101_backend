package com.sharavel.sharavel_be.comments.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sharavel.sharavel_be.comments.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
	Optional<Comment> findByUid(String uid);

	List<Comment> findByTargetTypeAndTargetUidAndParentIsNullAndDeletedFalseOrderByCreatedAtDesc(String targetType,
			String targetUid);

	List<Comment> findByParentAndDeletedFalseOrderByCreatedAtDesc(Comment parent);

	Long countByParentAndDeletedFalse(Comment parent);

	List<Comment> findByTargetTypeAndTargetUidOrderByCreatedAtDesc(String targetType, String targetUid);

	Long countByTargetTypeAndTargetUidAndDeletedFalse(String targetType, String targetUid);

	boolean existsByTargetTypeAndTargetUid(String targetType, String targetUid);

	boolean existsByUid(String uid);
}
