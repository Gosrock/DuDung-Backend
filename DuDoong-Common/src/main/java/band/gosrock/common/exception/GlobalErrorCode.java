package band.gosrock.common.exception;

import static band.gosrock.common.consts.DuDoongStatic.BAD_REQUEST;
import static band.gosrock.common.consts.DuDoongStatic.FORBIDDEN;
import static band.gosrock.common.consts.DuDoongStatic.INTERNAL_SERVER;
import static band.gosrock.common.consts.DuDoongStatic.NOT_FOUND;
import static band.gosrock.common.consts.DuDoongStatic.UNAUTHORIZED;

import band.gosrock.common.annotation.ExplainError;
import band.gosrock.common.dto.ErrorReason;
import java.lang.reflect.Field;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 글로벌 관련 예외 코드들이 나온 곳입니다. 인증 , global, aop 종류등 도메인 제외한 exception 코드들이 모이는 곳입니다. 도메인 관련 Exception
 * code 들은 도메인 내부 exception 패키지에 위치시키면 됩니다.
 */
@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements BaseErrorCode {
    EXAMPLE_NOT_FOUND(NOT_FOUND, "EXAMPLE_404_1", "Example Not Found."),

    ARGUMENT_NOT_VALID_ERROR(BAD_REQUEST, "GLOBAL_400_1", "validation error"),

    TOKEN_EXPIRED(UNAUTHORIZED, "AUTH_401_1", "Expired Jwt Token."),
    REFRESH_TOKEN_EXPIRED(FORBIDDEN, "AUTH_403_1", "refreshToken expired."),

    INVALID_TOKEN(UNAUTHORIZED, "GLOBAL-401-1", "Invalid Jwt Token."),

    INTERNAL_SERVER_ERROR(INTERNAL_SERVER, "GLOBAL-500-1", "Internal Server Error."),

    OTHER_SERVER_BAD_REQUEST(BAD_REQUEST, "FEIGN-400-1", "Other server bad request"),
    OTHER_SERVER_UNAUTHORIZED(BAD_REQUEST, "FEIGN-400-2", "Other server unauthorized"),
    OTHER_SERVER_FORBIDDEN(BAD_REQUEST, "FEIGN-400-3", "Other server forbidden"),
    OTHER_SERVER_EXPIRED_TOKEN(BAD_REQUEST, "FEIGN-400-4", "Other server expired token"),
    NOT_AVAILABLE_REDISSON_LOCK(500, "Redisson-500-1", "can not get redisson lock"),
    SECURITY_CONTEXT_NOT_FOUND(500, "GLOBAL-500-2", "security context not found"),

    TOSS_PAYMENTS_UNHANDLED(INTERNAL_SERVER, "PAYMENTS_INTERNAL_SERVER", "관리자에게 연락부탁드려요."),
    BAD_LOCK_IDENTIFIER(500, "AOP_500_1", "락의 키값이 잘못 세팅 되었습니다"),
    BAD_FILE_EXTENSION(BAD_REQUEST, "FILE-400-1", "파일 확장자가 잘못 되었습니다."),
    CART_NOT_FOUND(NOT_FOUND, "Cart-404-1", "Cart Not Found."),

    ISSUED_TICKET_NOT_FOUND(NOT_FOUND, "IssuedTicket-404-1", "IssuedTicket Not Found"),
    ISSUED_TICKET_NOT_MATCHED_USER(
            FORBIDDEN, "IssuedTicket-403-1", "IssuedTicket User Not Matched"),
    TOSS_PAYMENTS_ENUM_NOT_MATCH(INTERNAL_SERVER, "INFRA-500-1", "토스페이먼츠 이넘값 관련 매칭 안된 문제입니다."),

    EVENT_NOT_FOUND(NOT_FOUND, "Event-404-1", "Event Not Found"),
    HOST_NOT_AUTH_EVENT(FORBIDDEN, "Event-403-1", "Host Not Auth Event");
    private Integer status;
    private String code;
    private String reason;

    @Override
    public ErrorReason getErrorReason() {
        return ErrorReason.builder().reason(reason).code(code).status(status).build();
    }

    @Override
    public String getExplainError() throws NoSuchFieldException {
        Field field = this.getClass().getField(this.name());
        ExplainError annotation = field.getAnnotation(ExplainError.class);
        return Objects.nonNull(annotation) ? annotation.value() : this.getReason();
    }
}
