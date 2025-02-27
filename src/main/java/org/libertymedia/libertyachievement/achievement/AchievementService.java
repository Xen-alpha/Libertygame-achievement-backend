package org.libertymedia.libertyachievement.achievement;

import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.achievement.model.AchieveRequest;
import org.libertymedia.libertyachievement.achievement.model.QueryRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AchievementService {

    public void addAchievement(AchieveRequest request){

    }

    public List<QueryRequest> getAchievements(Long userId) {
        List<QueryRequest> result = new ArrayList<>();

        return result;
    }
}
