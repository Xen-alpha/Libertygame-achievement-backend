package org.libertymedia.libertyachievement.user;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.libertymedia.libertyachievement.user.model.request.PromotionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Operation(description="자신의 이름 확인")
    @GetMapping("/name")
    public ResponseEntity<String> getMyName(@AuthenticationPrincipal UserInfo user) {
        return ResponseEntity.ok(user.getUsername());
    }

    @Operation(description="로그인 리다이렉션")
    @PostMapping
    public ResponseEntity<String> loginSuccess(@AuthenticationPrincipal UserInfo user) {
        return ResponseEntity.ok("도전과제 서버 로그인 완료");
    }
    
    @Operation(description="로그아웃 리다이렉션")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
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
        return ResponseEntity.ok("승급 요청 기록됨, 등록된 이메일로 을 확인하세요!");
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
