package com.sharavel.sharavel_be.socialLink.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sharavel.sharavel_be.socialLink.dto.SocialLinkDto;
import com.sharavel.sharavel_be.socialLink.service.SocialLinkService;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/users")
public class SocialLinkController {

	@Autowired
	private SocialLinkService socialLinkService;

	@PostMapping("/setSocial")
	public ResponseEntity<String> setSocialLinks(@RequestBody List<SocialLinkDto> socialLinkDtoList) {
		try {
			socialLinkService.setSocialLinks(socialLinkDtoList);
			return ResponseEntity.ok("setSocialLinks successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to set social links");
		}
	}

	@GetMapping("/getSocial")
	public ResponseEntity<String> getSocialLinks(@RequestParam String uuid) {
		try {
			socialLinkService.getSocialLinks(uuid);
			return ResponseEntity.ok("Unfollowed successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to get social links");
		}
	}

	@PutMapping("/putSocial")
	public String putSocialLinks(@PathVariable String id, @RequestBody String entity) {
		// TODO: process PUT request

		return entity;
	}

	@DeleteMapping("/deleteSocial")
	public String deleteSocialLinks(@PathVariable String id, @RequestBody String entity) {
		// TODO: process PUT request

		return entity;
	}
}
