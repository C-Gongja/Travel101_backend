package com.sharavel.sharavel_be.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class TripEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity uid;

	@OneToMany(mappedBy = "tripId", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DaysEntity> days = new ArrayList<>();

	@Column(nullable = false)
	private String name;

	// startDate
	@Column(nullable = true)
	private Date startDate;
	// endDate
	@Column(nullable = true)
	private Date endDate;
	// total_cost
	@Column(nullable = false)
	private BigDecimal total_cost;
	// status (completed or not?)
	@Column(nullable = false)
	private boolean isCompleted;
	// scripts
	@Column(nullable = false)
	private Integer scripted;
	// countries[]
	@Column(nullable = false)
	private List<String> countries;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public UserEntity getUid() {
		return uid;
	}

	public void setUid(UserEntity uid) {
		this.uid = uid;
	}

	public List<DaysEntity> getDays() {
		return days;
	}

	public void setDays(List<DaysEntity> days) {
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

	public BigDecimal getTotal_cost() {
		return total_cost;
	}

	public void setTotal_cost(BigDecimal total_cost) {
		this.total_cost = total_cost;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public Integer getScripted() {
		return scripted;
	}

	public void setScripted(Integer scripted) {
		this.scripted = scripted;
	}

	public List<String> getCountries() {
		return countries;
	}

	public void setCountries(List<String> countries) {
		this.countries = countries;
	}
}
