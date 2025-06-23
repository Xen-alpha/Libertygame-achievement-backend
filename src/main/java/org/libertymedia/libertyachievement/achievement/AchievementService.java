package org.libertymedia.libertyachievement.achievement;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.achievement.model.Achievement;
import org.libertymedia.libertyachievement.achievement.model.Progress;
import org.libertymedia.libertyachievement.achievement.model.request.AchieveRequest;
import org.libertymedia.libertyachievement.achievement.model.request.AchievementRequest;
import org.libertymedia.libertyachievement.achievement.model.response.AchievementResponse;
import org.libertymedia.libertyachievement.achievement.model.response.QueryItemResponse;
import org.libertymedia.libertyachievement.achievement.model.response.QueryResponse;
import org.libertymedia.libertyachievement.user.UserRepository;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AchievementService {
    private final AchievementRepository achievementRepository;
    private final ProgressRepository progressRepository;
    private final UserRepository userRepository;

    // create, ADVANCED, achievement
    public String addAchievement(AchievementRequest request){
        Achievement achievement = achievementRepository.save(Achievement.builder().title(request.getTitle())
                .description(request.getDescription())
                .maxProgress(request.getMaxProgress())
                .build());
        return achievement.getTitle();
    }

    // read, permit all
    public QueryResponse getAchievements(String username) {
        UserInfo user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        List<Progress> progressEntities = user.getProgressList();
        List<QueryItemResponse> result = new ArrayList<>();
        for (Progress progressEntity : progressEntities) {
            Achievement achievement = progressEntity.getAchievement();
            QueryItemResponse queryItemResponse = QueryItemResponse.builder()
                    .title(achievement.getTitle()).description(achievement.getDescription())
                    .maxprogress(achievement.getMaxProgress())
                    .progress(progressEntity.getCurrentProgress())
                    .build();
            result.add(queryItemResponse);
        }
        return new QueryResponse(user.getUserIdx(),username, result);

    }

    // create, BASIC, progress
    @Transactional
    public AchievementResponse achieveProgress(AchieveRequest request){
        Achievement achievement = achievementRepository.findByTitle(request.getTitle()).orElse(null);
        UserInfo user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (achievement == null || user == null) {
            throw new RuntimeException("Cannot make achievement progress");
        }
        Progress progress = progressRepository.findByAchievementAndUser(achievement, user);
        if (progress == null) {
            progress = Progress.builder().achievement(achievement).currentProgress(1).user(user).build();
            progressRepository.save(progress);
        } else {
            if (progress.getCurrentProgress() < achievement.getMaxProgress()) {
                progress.setCurrentProgress(progress.getCurrentProgress() + 1);
                progressRepository.save(progress);
            }
        }
        return AchievementResponse.builder().title(achievement.getTitle())
                .description(achievement.getDescription()).progress(progress.getCurrentProgress())
                .maxprogress(achievement.getMaxProgress()).build();

    }

    @Transactional
    public void deleteAchievement(String achievementName){
        Achievement achievement = achievementRepository.findByTitle(achievementName).orElse(null);
        if(achievement != null){
            progressRepository.deleteAll(achievement.getUserProgresses());
            achievementRepository.delete(achievement);
        }
    }
}
