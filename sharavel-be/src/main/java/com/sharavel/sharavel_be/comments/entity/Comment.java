package com.sharavel.sharavel_be.comments.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sharavel.sharavel_be.user.entity.Users;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private final Long id;

	@Column(nullable = false, unique = true, updatable = false)
	private final String uid;

	@Column(nullable = false)
	private String targetType;

	@Column(nullable = false)
	private String targetUid;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Comment parent;

	@Column(nullable = false)
	private String content;

	@CreationTimestamp
	@Column(nullable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(nullable = true)
	private LocalDateTime updatedAt;

	@Column(nullable = false)
	private boolean deleted = false;

	// 양방향 매핑으로 대댓글 리스트도 가능 (optional)
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	private List<Comment> replies = new ArrayList<>();

	protected Comment() {
		this.id = null;
		this.uid = null;
	}

	private Comment(Builder builder) {
		this.id = null; // assigned by JPA
		this.uid = UUID.randomUUID().toString();
		this.targetType = Objects.requireNonNull(builder.targetType, "targetType is required");
		this.targetUid = Objects.requireNonNull(builder.targetUid, "targetUid is required");
		this.user = Objects.requireNonNull(builder.user, "User is required");
		this.content = Objects.requireNonNull(builder.content, "content is required");
		this.parent = builder.parent;
		this.createdAt = null;
		this.updatedAt = null;
		this.deleted = builder.deleted;
		this.replies = builder.replies != null ? new ArrayList<>(builder.replies) : new ArrayList<>();
	}

	public static class Builder {
		private String targetType;
		private String targetUid;
		private Users user;
		private Comment parent;
		private String content;
		private boolean deleted;
		private List<Comment> replies;

		public Builder(String targetType, String targetUid, Users user, String content) {
			this.targetType = Objects.requireNonNull(targetType, "targetType is required");
			this.targetUid = Objects.requireNonNull(targetUid, "targetUid is required");
			this.user = Objects.requireNonNull(user, "User is required");
			this.parent = null;
			this.content = Objects.requireNonNull(content, "content is required");
			this.replies = new ArrayList<>();
			this.deleted = false;
		}

		public Builder parent(Comment parent) {
			this.parent = parent;
			return this;
		}

		public Builder deleted(boolean deleted) {
			this.deleted = deleted;
			return this;
		}

		public Builder replies(List<Comment> replies) {
			this.replies = replies != null ? new ArrayList<>(replies) : new ArrayList<>();
			return this;
		}

		public Comment build() {
			return new Comment(this);
		}
	}

	// 양방향 관계 동기화를 위한 헬퍼 메서드
	public void addReply(Comment reply) {
		this.replies.add(reply);
		reply.setParent(this);
	}

	public void updateContent(String content, String authenticatedUserId) {
		if (!this.user.getUuid().equals(authenticatedUserId)) {
			throw new SecurityException("Unauthorized to update comment");
		}
		this.content = Objects.requireNonNull(content, "content is required");
	}

	public void markAsDeleted(String authenticatedUserId) {
		if (!this.user.getUuid().equals(authenticatedUserId)) {
			throw new SecurityException("Unauthorized to delete comment");
		}
		this.deleted = true;
	}

	public Long getId() {
		return id;
	}

	public String getUid() {
		return uid;
	}

	public String getTargetType() {
		return targetType;
	}

	public String getTargetUid() {
		return targetUid;
	}

	public Users getUser() {
		return user;
	}

	public Comment getParent() {
		return parent;
	}

	public String getContent() {
		return content;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public List<Comment> getReplies() {
		return replies;
	}

	public void setParent(Comment parent) {
		this.parent = parent;
	}

}
