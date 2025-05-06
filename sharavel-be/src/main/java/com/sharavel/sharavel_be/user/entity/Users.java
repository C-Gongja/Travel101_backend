package com.sharavel.sharavel_be.user.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sharavel.sharavel_be.follow.entity.UserFollow;
import com.sharavel.sharavel_be.socialLink.entity.SocialLink;
import com.sharavel.sharavel_be.trip.entity.Trip;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Table(name = "users")
@Entity
public class Users {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false, unique = true, updatable = false)
	private String uuid;

	@Column(nullable = true)
	private String provider;

	@Column(nullable = true)
	private String providerId;

	@Column(nullable = false)
	private String name;

	@Column(nullable = true)
	private String username;

	@Column(unique = true, length = 100, nullable = false)
	private String email;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SocialLink> socialLinks = new ArrayList<>();

	@Column(nullable = true)
	private String password;

	@Column(nullable = true)
	private String picture;

	@Column(nullable = true)
	private String country;

	@Column(nullable = false)
	private Integer totalTrips;

	@Column(nullable = false)
	private Integer totalTripDays;

	@Column(columnDefinition = "TEXT", nullable = true)
	private String bio;

	@OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<UserFollow> followers = new HashSet<>();

	@OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<UserFollow> following = new HashSet<>();

	@OneToMany(mappedBy = "uid", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Trip> trips = new ArrayList<>();

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	private Set<Roles> roles = new HashSet<>();

	@CreationTimestamp
	@Column(updatable = false, name = "created_at")
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	@PrePersist
	public void generateUUID() {
		if (this.uuid == null) {
			this.uuid = UUID.randomUUID().toString(); // UUID 자동 생성
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<SocialLink> getSocialLinks() {
		return socialLinks;
	}

	public void setSocialLinks(List<SocialLink> socialLinks) {
		this.socialLinks = socialLinks;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Roles> getRoles() {
		return roles;
	}

	public void setRoles(Set<Roles> roles) {
		this.roles = roles;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public List<Trip> getTrips() {
		return trips;
	}

	public void setTrips(List<Trip> trips) {
		this.trips = trips;
	}

	public Integer getTotalTrips() {
		return totalTrips;
	}

	public void setTotalTrips(Integer totalTrips) {
		this.totalTrips = totalTrips;
	}

	public Integer getTotalTripDays() {
		return totalTripDays;
	}

	public void setTotalTripDays(Integer totalTripDays) {
		this.totalTripDays = totalTripDays;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public Set<UserFollow> getFollowers() {
		return followers;
	}

	public void setFollowers(Set<UserFollow> followers) {
		this.followers = followers;
	}

	public Set<UserFollow> getFollowing() {
		return following;
	}

	public void setFollowing(Set<UserFollow> following) {
		this.following = following;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}
}