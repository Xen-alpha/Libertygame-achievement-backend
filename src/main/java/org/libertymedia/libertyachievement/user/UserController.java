package org.libertymedia.libertyachievement.user;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.libertymedia.libertyachievement.user.model.request.PromotionRequest;
import org.libertymedia.libertyachievement.user.model.response.TokenResponse;
import org.libertymedia.libertyachievement.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Operation(description="일반 리다이렉션")
    @PostMapping
    public ResponseEntity<String> enableAchievement() {
        return ResponseEntity.ok("등록 완료, 이제 도전과제 인증용 토큰을 받아올 수 있습니다.");
    }

    @Operation(description="리프레시 토큰으로 AccessToken 발급해서 리턴")
    @PostMapping("/issue")
    public ResponseEntity<TokenResponse> refresh(@CookieValue("RefreshTOKEN") String refresh) {
        UserInfo userInfo = JwtUtil.getUser(refresh); // 유효성 검사 + 블랙리스트/DB 확인(선택)
        String value = userService.getNewToken(refresh);
        if (value == null) return ResponseEntity.badRequest().body(new TokenResponse("Failed"));
        return ResponseEntity.ok().header("Authorization", value).body(new TokenResponse("Success"));
    }
    
    @Operation(description="로그아웃 리다이렉션")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals("libertyUserName")) {
                userService.deleteRefreshToken(c.getValue());
                break;
            }
        }
        return ResponseEntity.ok("도전과제 서버 로그아웃 완료");
    }

    @Operation(description="도전과제 제작자로 승급하는 요청을 테이블에 저장합니다.")
    @PostMapping("/promote")
    public ResponseEntity<String> promote(@AuthenticationPrincipal UserInfo user, @RequestBody PromotionRequest request) {
        try {
            userService.createPromotion(request);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
        return ResponseEntity.ok("승급 요청 기록됨, 등록된 이메일로 승급 확인 링크를 확인하세요!");
    }


    @Operation(description="도전과제 수행자에서 도전과제 제작자로 승급하는 요청을 수행합니다.")
    @GetMapping("/promote/accepted/{userIdx}")
    public ResponseEntity<String> promoteAccepted(@AuthenticationPrincipal UserInfo user, @PathVariable Long userIdx) {
        userService.promotionAccepted(userIdx);
        return ResponseEntity.ok("promoteAccepted");
    }

    @Operation(description="도전과제 제작자 승급 요청을 삭제할 때 사용합니다.")
    @GetMapping("/promote/declined/{userIdx}")
    public ResponseEntity<String> promoteDeclined(@AuthenticationPrincipal UserInfo user, @PathVariable Long userIdx) {
        userService.promotionRejected(userIdx);
        return ResponseEntity.ok("promoteDeclined");
    }


}
