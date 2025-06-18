package com.sharavel.sharavel_be.exceptions.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
	private int status; // HTTP 상태 코드 (예: 400, 401, 500)
	private String message; // 사용자에게 보여줄 에러 메시지
	private String errorCode; // (선택 사항) 내부적으로 구분할 에러 코드 (예: "EMAIL_NOT_FOUND")
	// private LocalDateTime timestamp; // (선택 사항) 에러 발생 시간

}
