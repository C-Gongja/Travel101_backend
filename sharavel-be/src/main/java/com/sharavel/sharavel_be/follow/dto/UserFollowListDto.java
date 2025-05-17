package com.sharavel.sharavel_be.follow.dto;

import com.sharavel.sharavel_be.user.dto.UserSnippetDto;

public class UserFollowListDto {
	private UserSnippetDto userSnippetDto;
	private boolean isNextPage;

	public UserFollowListDto(UserSnippetDto userSnippetDto, boolean isNextPage) {
		this.userSnippetDto = userSnippetDto;
		this.isNextPage = isNextPage;
	}

	public UserSnippetDto getUserSnippetDto() {
		return userSnippetDto;
	}

	public void setUserSnippetDto(UserSnippetDto userSnippetDto) {
		this.userSnippetDto = userSnippetDto;
	}

	public boolean isIsNextPage() {
		return isNextPage;
	}

	public void setIsNextPage(boolean isNextPage) {
		this.isNextPage = isNextPage;
	}
}
