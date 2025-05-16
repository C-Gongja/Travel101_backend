package com.sharavel.sharavel_be.follow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sharavel.sharavel_be.follow.entity.UserFollow;
import com.sharavel.sharavel_be.user.entity.Users;

public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
	Optional<UserFollow> findByFollowerAndFollowing(Users follower, Users following);

	List<UserFollow> findByFollower(Users follower);

	List<UserFollow> findByFollowing(Users following);

	boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

	Long countByFollower(Users follower);

	Long countByFollowing(Users following);

	void deleteByFollowerAndFollowing(Users follower, Users following);
}
