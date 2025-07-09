package org.libertymedia.libertyachievement.achievement;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.achievement.model.request.AchieveRequest;
import org.libertymedia.libertyachievement.achievement.model.request.AchievementRequest;
import org.libertymedia.libertyachievement.achievement.model.response.AchievementResponse;
import org.libertymedia.libertyachievement.common.BaseResponse;
import org.libertymedia.libertyachievement.achievement.model.response.QueryResponse;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="도전과제 관련 API", description="도전 과제 기능을 위해 요청해야 하는 URI들입니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/achievement/v0")
public class AchievementController {
    private final AchievementService achievementService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Operation(description="도전과제 항목을 생성합니다.")
    @PostMapping("/addition")
    public ResponseEntity<String> createAchievement(@AuthenticationPrincipal UserInfo user, @Valid @RequestBody AchievementRequest body) {
        logger.info("Creating achievement {}", body.getTitle());
        String result = achievementService.addAchievement(body);
        return ResponseEntity.ok("도전과제 등록 성공: "+ result);
    }

    @Operation(description="도전과제 진행도를 올립니다.")
    @PutMapping("/achieve")
    public ResponseEntity<AchievementResponse> createAchieve(@AuthenticationPrincipal UserInfo user, @Valid @RequestBody AchieveRequest body) {
        logger.info("Updating achievement for user {}", body.getUsername());
        AchievementResponse response =achievementService.achieveProgress(body);
        return ResponseEntity.ok(response);
    }

    @Operation(description="특정 사용자의 도전과제 진행도 목록을 봅니다.")
    @GetMapping("/list/{username}")
    public ResponseEntity<BaseResponse<QueryResponse>> getAchievements(@PathVariable String username) {
        BaseResponse<QueryResponse> response = BaseResponse.<QueryResponse>builder().success(true).result(achievementService.getAchievements(username)).build();
        return ResponseEntity.ok(response);
    }

    @Operation(description="도전과제 항목을 삭제합니다.")
    @DeleteMapping("/deletion/{title}")
    public ResponseEntity<String> deleteAchievement(@PathVariable String title) {
        logger.info("Deleting achievement {}", title);
        achievementService.deleteAchievement(title);
        return ResponseEntity.ok(title + " is successfully deleted");
    }
}
