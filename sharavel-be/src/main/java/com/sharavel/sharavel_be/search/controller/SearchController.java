package com.sharavel.sharavel_be.search.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sharavel.sharavel_be.search.service.SearchRepositoryCustom;
import com.sharavel.sharavel_be.trip.dto.TripListDto;

@RestController
@RequestMapping("/public")
public class SearchController {
	// private final SearchRepositoryCustom searchRepository;

	// public SearchController(SearchRepositoryCustom searchRepository) {
	// 	this.searchRepository = searchRepository;
	// }

	// @GetMapping("/search")
	// public List<TripListDto> search(@RequestParam String keyword) {
		
	// 	return searchRepository.searchAll(keyword);
	// }
}
