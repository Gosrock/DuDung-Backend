package band.gosrock.common.exception;

import static band.gosrock.common.consts.DuDoongStatic.BAD_REQUEST;
import static band.gosrock.common.consts.DuDoongStatic.FORBIDDEN;
import static band.gosrock.common.consts.DuDoongStatic.INTERNAL_SERVER;
import static band.gosrock.common.consts.DuDoongStatic.NOT_FOUND;
import static band.gosrock.common.consts.DuDoongStatic.UNAUTHORIZED;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    EXAMPLE_NOT_FOUND(NOT_FOUND, "EXAMPLE_404_1", "Example Not Found."),

    ARGUMENT_NOT_VALID_ERROR(BAD_REQUEST, "GLOBAL_400_1", "validation error"),

    TOKEN_EXPIRED(UNAUTHORIZED, "AUTH_401_1", "Expired Jwt Token."),
    REFRESH_TOKEN_EXPIRED(FORBIDDEN, "AUTH_403_1", "refreshToken expired."),

    INVALID_TOKEN(UNAUTHORIZED, "GLOBAL-401-1", "Invalid Jwt Token."),

    USER_NOT_FOUND(NOT_FOUND, "USER-404-1", "User Not Found."),

    INTERNAL_SERVER_ERROR(INTERNAL_SERVER, "GLOBAL-500-1", "Internal Server Error."),

    OTHER_SERVER_BAD_REQUEST(BAD_REQUEST, "FEIGN-400-1", "Other server bad request"),
    OTHER_SERVER_UNAUTHORIZED(BAD_REQUEST, "FEIGN-400-2", "Other server unauthorized"),
    OTHER_SERVER_FORBIDDEN(BAD_REQUEST, "FEIGN-400-3", "Other server forbidden"),
    OTHER_SERVER_EXPIRED_TOKEN(BAD_REQUEST, "FEIGN-400-4", "Other server expired token"),
    NOT_AVAILABLE_REDISSON_LOCK(500, "Redisson-500-1", "can not get redisson lock");
    private int status;
    private String code;
    private String reason;
}
