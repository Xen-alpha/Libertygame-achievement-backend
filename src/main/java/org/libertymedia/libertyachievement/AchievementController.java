package org.libertymedia.libertyachievement;

import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.model.AchieveRequest;
import org.libertymedia.libertyachievement.model.BaseResponse;
import org.libertymedia.libertyachievement.model.QueryRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/achievement/v0")
public class AchievementController {
    private final AchievementService achievementService;

    @PostMapping("/addition")
    public ResponseEntity<String> createAchievement(@RequestBody AchieveRequest body) {
        achievementService.addAchievement(body);
        return ResponseEntity.ok("도전과제 달성");
    }

    @GetMapping("/list/{idx}")
    public ResponseEntity<BaseResponse<List<QueryRequest>>> getAchievements(@PathVariable Long idx) {
        BaseResponse<List<QueryRequest>> response = BaseResponse.<List<QueryRequest>>builder().success(true).result(achievementService.getAchievements(idx)).build();
        return ResponseEntity.ok(response);
    }
}
