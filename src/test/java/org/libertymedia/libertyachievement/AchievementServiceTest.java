package org.libertymedia.libertyachievement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.libertymedia.libertyachievement.achievement.AchievementRepository;
import org.libertymedia.libertyachievement.achievement.AchievementService;
import org.libertymedia.libertyachievement.achievement.ProgressRepository;
import org.libertymedia.libertyachievement.achievement.model.Achievement;
import org.libertymedia.libertyachievement.achievement.model.Progress;
import org.libertymedia.libertyachievement.achievement.model.response.AchievementResponse;
import org.libertymedia.libertyachievement.user.UserRepository;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class AchievementServiceTest {

    @Mock
    private AchievementRepository achievementRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProgressRepository progressRepository;

    @InjectMocks
    private AchievementService achievementService;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
    }
    @DisplayName(value="Achievement Service Test")
    @Test
    public void testAchievementService() {
        when(achievementRepository.findByGameName("RPG in City")).thenReturn(
                List.of(
                        Achievement.builder().atitle("게임 클리어").adescription("축하합니다! 당신은 산토끼를 물리치고 도시에 평화를 가져다주었습니다!").gameName("RPG in City").maxProgress(1).build()
            )
        );
        when(userRepository.findByUsername("Senouis")).thenReturn(Optional.of(UserInfo.builder().idx(1L).userIdx(3L).username("Senouis").role( "BASIC").build()));
        when(progressRepository.findByAchievementAndUser(achievementRepository.findByGameName("RPG in City").get(0), userRepository.findByUsername("Senouis").orElse(null))).thenReturn(
                Progress.builder().currentProgress(1).build()
        );
        // test edit
        // test rate
        // test upload
        // test talk
        // test my game achivement
        List<AchievementResponse> myGameResult = achievementService.getGameAchievements("Senouis", "RPG in City");
        assertThat(myGameResult.size()).isEqualTo(1);
    }
}
