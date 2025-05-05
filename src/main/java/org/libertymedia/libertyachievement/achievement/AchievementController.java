package org.libertymedia.libertyachievement.achievement;

import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.achievement.model.request.AchieveRequest;
import org.libertymedia.libertyachievement.achievement.model.request.AchievementRequest;
import org.libertymedia.libertyachievement.common.BaseResponse;
import org.libertymedia.libertyachievement.achievement.model.response.QueryResponse;
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
    public ResponseEntity<String> createAchievement(@AuthenticationPrincipal UserInfo user, @RequestBody AchievementRequest body) {
        logger.info("Creating achievement {}", body.getTitle());
        String result = achievementService.addAchievement(body);
        return ResponseEntity.ok("도전과제 등록 성공: "+ result);
    }

    @PutMapping("/achieve")
    public ResponseEntity<String> createAchieve(@AuthenticationPrincipal UserInfo user, @RequestBody AchieveRequest body) {
        logger.info("Updating achievement for user {}", body.getUsername());
        achievementService.achieveProgress(body);
        return ResponseEntity.ok("도전과제 달성");
    }

    @GetMapping("/list/{username}")
    public ResponseEntity<BaseResponse<QueryResponse>> getAchievements(@PathVariable String username) {
        BaseResponse<QueryResponse> response = BaseResponse.<QueryResponse>builder().success(true).result(achievementService.getAchievements(username)).build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/deletion/{title}")
    public ResponseEntity<String> deleteAchievement(@PathVariable String title) {
        logger.info("Deleting achievement {}", title);
        achievementService.deleteAchievement(title);
        return ResponseEntity.ok(title + " is successfully deleted");
    }
}
