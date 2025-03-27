package com.example.shopping.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
	
    // 유저 관련 예외 코드
    USER_EMAIL_DUPLICATION("다른 유저와 이메일이 중복됩니다.", CONFLICT),
    USER_NAME_DUPLICATION("다른 유저와 이름이 중복됩니다.", CONFLICT),
    USER_NOT_LOGIN("로그인이 필요합니다. 로그인을 해주세요.", UNAUTHORIZED),
    USER_NOT_FOUND("해당하는 유저를 찾을 수 없습니다.", NOT_FOUND),
    INVALID_PASSWORD("패스워드가 올바르지 않습니다.", BAD_REQUEST),
    PASSWORD_SAME_AS_OLD("이전 패스워드와 동일할 수 없습니다.", BAD_REQUEST),
    USER_ACCESS_DENIED("사용자가 접근할 수 있는 권한이 없습니다.", FORBIDDEN),
    USER_ROLE_SAME_AS_OLD("이전 역활과 동일할 수 없습니다.", BAD_REQUEST),
    INVALID_USER_ROLE("유효하지 않는 role 입니다.", BAD_REQUEST),
	
    // 토큰 관련 예외 코드
    TOKEN_NOT_FOUND("해당 토큰을 찾을 수 없습니다.", NOT_FOUND),
    INVALID_TOKEN("유효하지 않은 토큰입니다.", UNAUTHORIZED),
	
    // 주문 관련 예외 코드
    ORDER_NOT_FOUND("해당 주문을 찾을 수 없습니다.", NOT_FOUND),
    CART_NOT_FOUND("해당 카트를 찾을 수 없습니다.", NOT_FOUND),
    ORDER_ALREADY_FINISH("해당 주문이 이미 완료 상태입니다.", BAD_REQUEST),
    QUANTITY_CAN_NOT_MINUS("개수가 음수가 될 수 없습니다.", BAD_REQUEST),
	
    // 쿠폰 관련 예외 코드
    COUPON_NOT_FOUND("해당 쿠폰을 찾을 수 없습니다.", NOT_FOUND),
    ALREADY_PUBLISHED_COUPON("쿠폰 발급 이력이 있습니다.", BAD_REQUEST),
    EMPTY_COUPON_STOCK("쿠폰 수량이 0입니다.", CONFLICT),
    AlREADY_USED_COUPON("이미 사용된 쿠폰입니다", BAD_REQUEST),
    NOT_SAME_MD("쿠폰 담당 MD가 아닙니다.", BAD_REQUEST),
    FAILED_TO_GAIN_LOCK("락 획득에 실패했습니다.", LOCKED),

    // 상품 관련 예외 코드
    PRODUCT_NOT_FOUND("해당 상품을 찾을 수 없습니다.", NOT_FOUND),
    OUT_OF_STOCK("해당 제품의 재고가 부족합니다.", BAD_REQUEST),
	
    // 리뷰 관련 예외 코드
    REVIEW_NOT_FOUND("해당 리뷰를 찾을 수 없습니다.", NOT_FOUND);
	
    private final String message;
    private final HttpStatus status;
}
