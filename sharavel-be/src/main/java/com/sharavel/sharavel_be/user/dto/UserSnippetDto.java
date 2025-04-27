package com.sharavel.sharavel_be.user.dto;

public class UserSnippetDto {
	private String uuid;
	private String name;
	private String username;

	public UserSnippetDto(String uuid, String name, String username) {
		this.uuid = uuid;
		this.name = name;
		this.username = username;
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

}
