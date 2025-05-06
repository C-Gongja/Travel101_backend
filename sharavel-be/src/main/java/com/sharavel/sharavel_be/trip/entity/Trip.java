package com.sharavel.sharavel_be.trip.entity;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sharavel.sharavel_be.countries.entity.Country;
import com.sharavel.sharavel_be.user.entity.Users;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

@Entity
public class Trip {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable = false, unique = true, updatable = false)
	private String tripUid;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private Users uid;

	@OneToMany(mappedBy = "tripId", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Days> days = new ArrayList<>();

	@Column(nullable = false)
	private String name;

	@Column(nullable = true)
	private Date startDate;

	@Column(nullable = true)
	private Date endDate;

	// planned, ongoing, completed
	@Column(nullable = false)
	private boolean isCompleted;

	@Column(nullable = false)
	private Long scripted;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "trip_countries", joinColumns = @JoinColumn(name = "trip_id"), inverseJoinColumns = @JoinColumn(name = "country_iso2"))
	private Set<Country> countries = new HashSet<>();

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

	public String getTripUid() {
		return tripUid;
	}

	public void setTripUid(String tripUid) {
		this.tripUid = tripUid;
	}

	@PrePersist
	public void generateUUID() {
		if (this.tripUid == null) {
			this.tripUid = UUID.randomUUID().toString(); // UUID 자동 생성
		}
	}

	public Users getUid() {
		return uid;
	}

	public void setUid(Users uid) {
		this.uid = uid;
	}

	public List<Days> getDays() {
		return days;
	}

	public void setDays(List<Days> days) {
		this.days = days;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public Long getScripted() {
		return scripted;
	}

	public void setScripted(Long scripted) {
		this.scripted = scripted;
	}

	public Set<Country> getCountries() {
		return countries;
	}

	public void setCountries(Set<Country> countries) {
		this.countries = countries;
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
}
