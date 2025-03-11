package org.libertymedia.libertyachievement.achievement;

import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.achievement.model.AchieveRequest;
import org.libertymedia.libertyachievement.achievement.model.BaseResponse;
import org.libertymedia.libertyachievement.achievement.model.QueryRequest;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/achievement/v0")
public class AchievementController {
    private final AchievementService achievementService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping("/addition")
    public ResponseEntity<String> createAchievement(@AuthenticationPrincipal UserInfo user, @RequestBody AchieveRequest body) {
        logger.info("Creating achievement for user {}", body.getUserId());
        achievementService.addAchievement(body);
        return ResponseEntity.ok("도전과제 달성");
    }

    @GetMapping("/list/{idx}")
    public ResponseEntity<BaseResponse<List<QueryRequest>>> getAchievements(@PathVariable Long idx) {
        logger.info("Listing achievement for user {}", idx);
        BaseResponse<List<QueryRequest>> response = BaseResponse.<List<QueryRequest>>builder().success(true).result(achievementService.getAchievements(idx)).build();
        return ResponseEntity.ok(response);
    }
}
