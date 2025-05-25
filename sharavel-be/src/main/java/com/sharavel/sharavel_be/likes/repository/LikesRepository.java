package com.sharavel.sharavel_be.likes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sharavel.sharavel_be.user.entity.Users;
import com.sharavel.sharavel_be.likes.entity.Likes;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
	boolean existsByUser(Users user);

	boolean existsByTargetTypeAndTargetUid(String targetType, String targetUid);

	Optional<Likes> findByTargetTypeAndTargetUidAndUser(String targetType, String targetUid, Users user);

	boolean existsByTargetTypeAndTargetUidAndUser(String targetType, String targetUid, Users user);

	Long countByTargetTypeAndTargetUid(String targetType, String targetUid);
}
