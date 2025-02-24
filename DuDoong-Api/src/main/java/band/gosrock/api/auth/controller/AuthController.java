package band.gosrock.api.auth.controller;


import band.gosrock.api.auth.model.dto.request.RegisterRequest;
import band.gosrock.api.auth.model.dto.response.AvailableRegisterResponse;
import band.gosrock.api.auth.model.dto.response.OauthLoginLinkResponse;
import band.gosrock.api.auth.model.dto.response.OauthTokenResponse;
import band.gosrock.api.auth.model.dto.response.OauthUserInfoResponse;
import band.gosrock.api.auth.model.dto.response.TokenAndUserResponse;
import band.gosrock.api.auth.service.LoginUseCase;
import band.gosrock.api.auth.service.LogoutUseCase;
import band.gosrock.api.auth.service.OauthUserInfoUseCase;
import band.gosrock.api.auth.service.RefreshUseCase;
import band.gosrock.api.auth.service.RegisterUseCase;
import band.gosrock.api.auth.service.WithDrawUseCase;
import band.gosrock.api.auth.service.helper.CookieHelper;
import band.gosrock.api.config.rateLimit.UserRateLimiter;
import band.gosrock.common.annotation.ApiErrorCodeExample;
import band.gosrock.common.annotation.DevelopOnlyApi;
import band.gosrock.infrastructure.outer.api.oauth.exception.KakaoKauthErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "1-1. [인증]")
public class AuthController {

    private final RegisterUseCase registerUseCase;

    private final LoginUseCase loginUseCase;
    private final RefreshUseCase refreshUseCase;

    private final OauthUserInfoUseCase oauthUserInfoUseCase;

    private final WithDrawUseCase withDrawUseCase;
    private final LogoutUseCase logoutUseCase;

    private final CookieHelper cookieHelper;

    private final UserRateLimiter rateLimiter;

    @Operation(summary = "kakao oauth 링크발급 (백엔드용 )", description = "kakao 링크를 받아볼수 있습니다.")
    @Tag(name = "1-2. [카카오]")
    @GetMapping("/oauth/kakao/link/test")
    public OauthLoginLinkResponse getKakaoOauthLinkTest() {
        return registerUseCase.getKaKaoOauthLinkTest();
    }

    @Operation(summary = "kakao oauth 링크발급 (클라이언트용)", description = "kakao 링크를 받아볼수 있습니다.")
    @Tag(name = "1-2. [카카오]")
    @GetMapping("/oauth/kakao/link")
    public OauthLoginLinkResponse getKakaoOauthLink(
            @RequestHeader(value = "referer", required = false) String referer,
            @RequestHeader(value = "host", required = false) String host) {
        // 스테이징, prod 서버에 배포된 클라이언트에 해당
        if (referer.contains(host)) {
            log.info("/oauth/kakao" + host);
            String format = String.format("https://%s", host);
            if (referer.contains("admin")) {
                // 프론트 개발자가 로컬에서 개발 테스트 할 때 해당 https://dudoong.com/admin
                return registerUseCase.getKaKaoOauthLink(format + "/admin");
            }
            return registerUseCase.getKaKaoOauthLink(format);
        } else if (referer.contains("5173")) {
            // 프론트 개발자가 로컬에서 개발 테스트 할 때 해당 https://localhost:5173/admin
            return registerUseCase.getKaKaoOauthLink(referer + "/admin");
        }
        // 프론트 개발자가 로컬에서 개발 테스트 할 때 해당 https://localhost:3000
        return registerUseCase.getKaKaoOauthLink(referer);
    }

    @Operation(summary = "카카오 code 요청받는 곳입니다. referer,host는 건들이지 말아주세요!안보내셔도됩니다.")
    @Tag(name = "1-2. [카카오]")
    @GetMapping("/oauth/kakao")
    @ApiErrorCodeExample(KakaoKauthErrorCode.class)
    public OauthTokenResponse getCredentialFromKaKao(
            @RequestParam("code") String code,
            @RequestHeader(value = "referer", required = false) String referer,
            @RequestHeader(value = "host", required = false) String host) {
        // 스테이징, prod 서버에 배포된 클라이언트에 해당
        if (referer.contains(host)) {
            log.info("/oauth/kakao" + host);
            String format = String.format("https://%s", host);
            if (referer.contains("admin")) {
                return registerUseCase.getCredentialFromKaKao(code, format + "/admin");
            }
            return registerUseCase.getCredentialFromKaKao(code, format);
        } else if (referer.contains("5173")) {
            // 프론트 개발자가 로컬에서 어드민 테스트 할 때 해당 https://localhost:5173/admin
            return registerUseCase.getCredentialFromKaKao(code, referer + "/admin");
        }
        // 프론트 개발자가 로컬에서 개발 테스트 할 때 해당 https://localhost:3000/admin
        return registerUseCase.getCredentialFromKaKao(code, referer);
    }

    @Operation(summary = "개발용 회원가입입니다 클라이언트가 몰라도 됩니다.", deprecated = true)
    @Tag(name = "1-2. [카카오]")
    @DevelopOnlyApi
    @GetMapping("/oauth/kakao/develop")
    public ResponseEntity<TokenAndUserResponse> developUserSign(@RequestParam("code") String code) {
        TokenAndUserResponse tokenAndUserResponse = registerUseCase.upsertKakaoOauthUser(code);
        return ResponseEntity.ok()
                .headers(cookieHelper.getTokenCookies(tokenAndUserResponse))
                .body(tokenAndUserResponse);
    }

    @Operation(summary = "회원가입이 가능한지 id token 으로 확인합니다.")
    @Tag(name = "1-2. [카카오]")
    @GetMapping("/oauth/kakao/register/valid")
    public AvailableRegisterResponse kakaoAuthCheckRegisterValid(
            @RequestParam("id_token") String token) {
        return registerUseCase.checkAvailableRegister(token);
    }

    @Operation(summary = "id_token 으로 회원가입을 합니다.")
    @Tag(name = "1-2. [카카오]")
    @PostMapping("/oauth/kakao/register")
    public ResponseEntity<TokenAndUserResponse> kakaoAuthCheckRegisterValid(
            @RequestParam("id_token") String token,
            @Valid @RequestBody RegisterRequest registerRequest) {
        TokenAndUserResponse tokenAndUserResponse =
                registerUseCase.registerUserByOCIDToken(token, registerRequest);
        return ResponseEntity.ok()
                .headers(cookieHelper.getTokenCookies(tokenAndUserResponse))
                .body(tokenAndUserResponse);
    }

    @NotNull
    @Operation(summary = "id_token 으로 로그인을 합니다.")
    @Tag(name = "1-2. [카카오]")
    @PostMapping("/oauth/kakao/login")
    public ResponseEntity<TokenAndUserResponse> kakaoOauthUserLogin(
            @RequestParam("id_token") String token) {
        TokenAndUserResponse tokenAndUserResponse = loginUseCase.execute(token);
        return ResponseEntity.ok()
                .headers(cookieHelper.getTokenCookies(tokenAndUserResponse))
                .body(tokenAndUserResponse);
    }

    @Operation(summary = "accessToken 으로 oauth user 정보를 가져옵니다.")
    @Tag(name = "1-2. [카카오]")
    @PostMapping("/oauth/kakao/info")
    public OauthUserInfoResponse kakaoOauthUserInfo(
            @RequestParam("access_token") String accessToken) {
        return oauthUserInfoUseCase.execute(accessToken);
    }

    @Operation(summary = "refreshToken 용입니다.")
    @PostMapping("/token/refresh")
    public ResponseEntity<TokenAndUserResponse> tokenRefresh(
            @CookieValue(value = "refreshToken", required = false) String refreshTokenCookie,
            @RequestParam(value = "token", required = false, defaultValue = "")
                    String refreshToken) {

        // 쿠키 우선시해서 리프레쉬.
        TokenAndUserResponse tokenAndUserResponse =
                refreshUseCase.execute(
                        refreshTokenCookie != null ? refreshTokenCookie : refreshToken);
        return ResponseEntity.ok()
                .headers(cookieHelper.getTokenCookies(tokenAndUserResponse))
                .body(tokenAndUserResponse);
    }

    @Operation(summary = "회원탈퇴를 합니다.")
    @SecurityRequirement(name = "access-token")
    @DeleteMapping("/me")
    public ResponseEntity withDrawUser() {
        withDrawUseCase.execute();
        return ResponseEntity.ok().headers(cookieHelper.deleteCookies()).body(null);
    }

    @Operation(summary = "로그아웃을 합니다.")
    @SecurityRequirement(name = "access-token")
    @PostMapping("/logout")
    public ResponseEntity logoutUser() {
        logoutUseCase.execute();
        return ResponseEntity.ok().headers(cookieHelper.deleteCookies()).body(null);
    }
}
