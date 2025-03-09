package com.sharavel.sharavel_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class DaysEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "trip_id", nullable = false)
	private TripEntity tripId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public TripEntity getTripId() {
		return tripId;
	}

	public void setTripId(TripEntity tripId) {
		this.tripId = tripId;
	}

	// daynum
	// name?
	// date
	// cost
	// locations[] (country state city)? how to manage this location with trip
	// location.
}
