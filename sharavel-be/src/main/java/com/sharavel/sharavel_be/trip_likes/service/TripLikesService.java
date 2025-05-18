package com.sharavel.sharavel_be.trip_likes.service;

public interface TripLikesService {
	public String addLike(String tripUid);

	public String removeLike(String tripUid);

	public int getTripLikes(String tripUid);
}
