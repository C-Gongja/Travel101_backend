package com.sharavel.sharavel_be.user.dto;

public class UserSnippetDto {
	private String uuid;
	private String name;
	private String username;
	private boolean isFollowing;

	public UserSnippetDto(String uuid, String name, String username, boolean isFollowing) {
		this.uuid = uuid;
		this.name = name;
		this.username = username;
		this.isFollowing = isFollowing;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public boolean isIsFollowing() {
		return isFollowing;
	}

	public void setIsFollowing(boolean isFollowing) {
		this.isFollowing = isFollowing;
	}

}
