package com.sharavel.sharavel_be.trip_script.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.sharavel.sharavel_be.trip.entity.Trip;
import com.sharavel.sharavel_be.user.entity.Users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;

@Entity
public class TripScript {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false, unique = true, updatable = false)
	private String uid;

	@PrePersist
	public void generateUUID() {
		if (this.uid == null) {
			this.uid = UUID.randomUUID().toString(); // UUID 자동 생성
		}
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "trip_id")
	private Trip trip;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users scriptUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "copied_trip_id")
	private Trip copiedTrip;

	private LocalDateTime scriptedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Trip getTrip() {
		return trip;
	}

	public void setTrip(Trip trip) {
		this.trip = trip;
	}

	public Users getScriptUser() {
		return scriptUser;
	}

	public void setScriptUser(Users scriptUser) {
		this.scriptUser = scriptUser;
	}

	public LocalDateTime getScriptedAt() {
		return scriptedAt;
	}

	public void setScriptedAt(LocalDateTime scriptedAt) {
		this.scriptedAt = scriptedAt;
	}

	public Trip getCopiedTrip() {
		return copiedTrip;
	}

	public void setCopiedTrip(Trip copiedTrip) {
		this.copiedTrip = copiedTrip;
	}

}
