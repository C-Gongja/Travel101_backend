package com.sharavel.sharavel_be.socialLink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sharavel.sharavel_be.socialLink.entity.SocialLink;
import com.sharavel.sharavel_be.user.entity.Users;

@Repository
public interface SocialLinkRepository extends JpaRepository<SocialLink, Long> {
	List<SocialLink> findByUser(Users user);

	void deleteByUser(Users user);
}
