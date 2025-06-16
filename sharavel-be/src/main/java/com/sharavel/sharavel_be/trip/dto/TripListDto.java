package com.sharavel.sharavel_be.trip.dto;

import java.time.LocalDate;
import java.util.List;

import com.sharavel.sharavel_be.countries.dto.CountryDto;
import com.sharavel.sharavel_be.s3bucket.dto.response.S3TripMediaResponse;

public class TripListDto {
	private String tripUid;
	private String name;
	private String username;
	private LocalDate startDate;
	private LocalDate endDate;
	private List<CountryDto> countries;
	private boolean isCompleted;
	private Long likesCount;
	private Long commentsCount;
	private Long scriptedCount;
	private List<S3TripMediaResponse> media;

	public TripListDto() {
	}

	public TripListDto(String tripUid, String name, String username, LocalDate startDate, LocalDate endDate,
			List<CountryDto> countries, boolean isCompleted, Long scriptedCount, List<S3TripMediaResponse> media) {
		this.tripUid = tripUid;
		this.username = username;
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.countries = countries;
		this.isCompleted = isCompleted;
		this.scriptedCount = scriptedCount;
		this.media = media;
	}

	// get trip uid
	public String getTripUid() {
		return tripUid;
	}

	public void setTripUid(String tripUid) {
		this.tripUid = tripUid;
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

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public List<CountryDto> getCountries() {
		return countries;
	}

	public void setCountries(List<CountryDto> countries) {
		this.countries = countries;
	}

	public Long getLikesCount() {
		return likesCount;
	}

	public void setLikesCount(Long likesCount) {
		this.likesCount = likesCount;
	}

	public Long getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(Long commentsCount) {
		this.commentsCount = commentsCount;
	}

	public Long getScriptedCount() {
		return scriptedCount;
	}

	public void setScriptedCount(Long scriptedCount) {
		this.scriptedCount = scriptedCount;
	}

	public boolean isIsCompleted() {
		return isCompleted;
	}

	public void setIsCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public List<S3TripMediaResponse> getMedia() {
		return media;
	}

	public void setMedia(List<S3TripMediaResponse> media) {
		this.media = media;
	}

	@Override
	public String toString() {
		return "TripDto{" +
				"username='" + username + '\'' +
				"tripName=" + name +
				", startDate=" + startDate +
				", endDate=" + endDate +
				", scripted=" + scriptedCount +
				", countries=" + countries +
				'}';
	}
}
