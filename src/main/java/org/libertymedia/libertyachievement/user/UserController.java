package org.libertymedia.libertyachievement.user;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    
    @Operation(description="로그아웃 리다이렉션")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("logout");
    }

    @Operation(description="도전과제 수행자에서 도전과제 제작자로 승급하는 요청을 수행합니다.")
    @GetMapping("/promote/accepted/{userIdx}")
    public ResponseEntity<String> promoteAccepted(@PathVariable Long userIdx) {
        userService.promotionAccepted(userIdx);
        return ResponseEntity.ok("promoteAccepted");
    }

    @Operation(description="도전과제 제작자 승급 요청을 삭제할 때 사용합니다.")
    @GetMapping("/promote/declined/{userIdx}")
    public ResponseEntity<String> promoteDeclined(@PathVariable Long userIdx) {
        userService.promotionRejected(userIdx);
        return ResponseEntity.ok("promoteDeclined");
    }
}
