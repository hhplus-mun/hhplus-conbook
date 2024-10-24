package io.hhplus.conbook.interfaces.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // --- 사용자 도메인 Error ---

    USER_NOT_FOUND("1001", "사용자 정보를 찾을 수 없습니다."),

    // --- 콘서트 도메인 Error ---

    CONCERT_NOT_FOUND("2001", "콘서트 정보를 찾을 수 없습니다."),
    CONCERT_SCHEDULE_NOT_FOUND("2002", "콘서트 일정 정보를 찾을 수 없습니다."),
    CONCERT_UNAUTHORIZED_ACCESS("2003", "해당 콘서트에 대한 권한이 없습니다."),
    CONCERT_DATE_FOMRAT("2004", "날짜 형식이 유효하지 않습니다. (yyMMdd 형식이어야 합니다)"),
    SEAT_NOT_FOUND("2005", "좌석 정보를 찾을 수 없습니다."),

    // --- 예약 도메인 Error ---

    BOOKING_NOT_FOUND("3001", "예약정보를 찾을 수 없습니다."),
    INVALID_BOOKING_STATUS("3002", "적합하지 않은 예약상태입니다."),
    NOT_AVAILABLE_SEAT("3003", "예약할 수 없는 좌석입니다."),

    // --- 포인트 도메인 Error ---

    POINT_INFO_NOT_FOUND("4001", "사용자의 포인트 정보를 찾을 수 없습니다."),
    INVALID_POINT_REQUEST("4002", "유효하지 않은 포인트 처리 요청입니다."),
    INSUFFICIENT_POINT_BALANCE("4003", "사용자의 포인트 잔고가 부족합니다."),

    // --- 토큰 도메인 Error ---

    TOKEN_ALREADY_EXIST("5001", "해당 콘서트에 대한 사용자 토큰이 이미 존재합니다."),
    QUEUE_NOT_FOUND("5002", "해당 콘서트에 대한 대기열을 찾을 수 없습니다."),
    TOKEN_NOT_FOUND("5003", "해당 콘서트에 대한 사용자의 토큰 정보를 찾을 수 없습니다."),
    ACCESS_TOKEN_VALIDATION_FAILED("5004", "ACCESS 토큰 검증에 실패하였습니다."),
    WAITING_TOKEN_VALIDATION_FAILED("5005", "WAITING 토큰 검증에 실패하였습니다."),

    // --- 필터 Error ---
    
    UNAUTHORIZED_ACCESS("6001", "허가되지 않은 접근입니다."),

    // --- Server Error ---

    INTERNAL_SERVER_ERROR("500", "Internal Server Error");

    private String code;
    private String desc;

    public static ErrorCode resolve(String errorMessage) {
        for (ErrorCode errorCode : values()) {
            if(errorCode.code.equals(errorMessage)) return errorCode;
        }

        return INTERNAL_SERVER_ERROR;
    }
}
