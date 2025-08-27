package org.libertymedia.libertyachievement.achievement;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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
        // 우선 사용자가 존재하는지 확인
        UserInfo user = userRepository.findByUsername(request.getCreatedBy()).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("Wrong Creator name.");
        }
        Achievement achievement = achievementRepository.save(Achievement.builder().atitle(request.getTitle())
                .adescription(request.getDescription())
                .maxProgress(request.getMaxProgress())
                .build());
        return achievement.getAtitle();
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
                    .title(achievement.getAtitle()).description(achievement.getAdescription())
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
        Achievement achievement = achievementRepository.findByAtitle(request.getTitle()).orElse(null);
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
        return AchievementResponse.builder().title(achievement.getAtitle())
                .description(achievement.getAdescription()).progress(progress.getCurrentProgress())
                .maxprogress(achievement.getMaxProgress()).build();

    }

    @Transactional
    public void deleteAchievement(String achievementName){
        Achievement achievement = achievementRepository.findByAtitle(achievementName).orElse(null);
        if(achievement != null){
            progressRepository.deleteAll(achievement.getUserProgresses());
            achievementRepository.delete(achievement);
        }
    }

    public List<AchievementResponse> processAchievement(List<Achievement> targets, String username) {
        UserInfo user = userRepository.findByUsername(username).orElseThrow();
        List<AchievementResponse> result = new ArrayList<>();
        for (Achievement achievement : targets) {
            Progress progress = progressRepository.findByAchievementAndUser(achievement, user);
            if (progress.getCurrentProgress() >= achievement.getMaxProgress()) {
                continue;
            }
            progress.setCurrentProgress(progress.getCurrentProgress() + 1);
            progressRepository.save(progress);
            result.add(AchievementResponse.from(achievement,progress));
        }
        return result;
    }

    public List<AchievementResponse> achieveEditProgress(@Valid AchieveRequest body) {
        List<Achievement> targets = achievementRepository.findByCreatedBy("Achieve_Edit");
        return processAchievement(targets, body.getUsername());
    }

    public List<AchievementResponse> achieveRateProgress(@Valid AchieveRequest body) {
        List<Achievement> targets = achievementRepository.findByCreatedBy("Achieve_Rate");
        return processAchievement(targets, body.getUsername());
    }

    public List<AchievementResponse> achieveFileProgress(@Valid AchieveRequest body) {
        List<Achievement> targets = achievementRepository.findByCreatedBy("Achieve_Upload");
        return processAchievement(targets, body.getUsername());
    }

    public List<AchievementResponse> achieveTalkProgress(@Valid AchieveRequest body) {
        List<Achievement> targets = achievementRepository.findByCreatedBy("Achieve_Talk");
        return processAchievement(targets, body.getUsername());
    }

    public List<AchievementResponse> getGameAchievements(String username, String gameName) {
        List<Achievement> targets = achievementRepository.findByGameName(gameName);
        UserInfo user = userRepository.findByUsername(username).orElse(null);
        List<AchievementResponse> result = new ArrayList<>();
        if (user == null) {
            return result;
        }
        for (Achievement achievement : targets) {
            Progress progress = progressRepository.findByAchievementAndUser(achievement, user);
            result.add(AchievementResponse.from(achievement,progress));
        }
        return result;
    }
}
