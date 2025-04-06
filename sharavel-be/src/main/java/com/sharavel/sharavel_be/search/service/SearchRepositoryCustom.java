package com.sharavel.sharavel_be.search.service;

import java.util.List;

import com.sharavel.sharavel_be.search.dto.SearchDto;

public interface SearchRepositoryCustom {
	List<SearchDto> searchAll(String keyword);
}
