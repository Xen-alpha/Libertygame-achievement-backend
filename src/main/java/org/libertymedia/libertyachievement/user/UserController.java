package org.libertymedia.libertyachievement.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("logout");
    }

    @GetMapping("/promote/accepted/{userIdx}")
    public ResponseEntity<String> promoteAccepted(@PathVariable Long userIdx) {
        userService.promotionAccepted(userIdx);
        return ResponseEntity.ok("promoteAccepted");
    }

    @GetMapping("/promote/declined/{userIdx}")
    public ResponseEntity<String> promoteDeclined(@PathVariable Long userIdx) {
        userService.promotionRejected(userIdx);
        return ResponseEntity.ok("promoteDeclined");
    }
}
