package com.sharavel.sharavel_be.socialLink.dto;

public class SocialLinkDto {
	private String platform;
	private String url;

	public SocialLinkDto(String platform, String url) {
		this.platform = platform;
		this.url = url;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
