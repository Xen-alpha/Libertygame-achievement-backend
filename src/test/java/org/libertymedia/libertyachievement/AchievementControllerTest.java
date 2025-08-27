package org.libertymedia.libertyachievement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.libertymedia.libertyachievement.achievement.AchievementController;
import org.libertymedia.libertyachievement.achievement.AchievementService;
import org.libertymedia.libertyachievement.achievement.model.request.AchieveRequest;
import org.libertymedia.libertyachievement.achievement.model.response.AchievementResponse;
import org.libertymedia.libertyachievement.achievement.model.response.QueryItemResponse;
import org.libertymedia.libertyachievement.achievement.model.response.QueryResponse;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

public class AchievementControllerTest {
    @Mock
    private AchievementService achievementService;

    // 아직 추가하지 않은 서비스가 있다면 아래처럼 목업으로 만들어주세요
    // @Mock
    // private ExampleService exService;

    @InjectMocks
    private AchievementController achievementController;

    private AchieveRequest editAchievementRequest;

    // 미리 세팅해야 하는 특정한 값은 여기에
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        editAchievementRequest = new AchieveRequest("10회 편집 달성", "MediaWiki_Default");
    }

    // 응답 시간 문제가 있으면 @Timeout() 걸 것

    @DisplayName(value="Achievement API Test")
    @Test
    public void controllerTest() {
        // Controller Test

        when(achievementService.achieveEditProgress(editAchievementRequest)).thenReturn(
                List.of(AchievementResponse.builder().title("10회 편집 달성").description("축하합니다! 10회 편집 횟수를 달성했습니다!").progress(1).maxprogress(10).build(),
                        AchievementResponse.builder().title("100회 편집 달성").description("축하합니다! 100회 편집 횟수를 달성했습니다!").progress(1).maxprogress(100).build(),
                        AchievementResponse.builder().title("1000회 편집 달성").description("축하합니다! 1000회 편집 횟수를 달성했습니다!").progress(1).maxprogress(1000).build(),
                        AchievementResponse.builder().title("10000회 편집 달성").description("어떻게 여기까지 온 거죠? 10000회 편집 횟수를 달성했습니다!").progress(1).maxprogress(10000).build(),
                        AchievementResponse.builder().title("42회 편집 달성").description("모든 것의 답안이 되는 42회 편집 횟수를 달성했습니다!").progress(1).maxprogress(42).build(),
                        AchievementResponse.builder().title("300회 편집 달성").description("This is SPARTAAAAA!!! 300회 편집 횟수를 달성했습니다!").progress(1).maxprogress(300).build(),
                        AchievementResponse.builder().title("500회 편집 달성").description("축하합니다! 500회 편집 횟수를 달성했습니다!").progress(1).maxprogress(500).build(),
                        AchievementResponse.builder().title("5000회 편집 달성").description("어떻게 여기까지 온 거죠? 5000회 편집 횟수를 달성했습니다!").progress(1).maxprogress(5000).build()
                )
        );
        when(achievementService.getAchievements("Senouis")).thenReturn(
                new QueryResponse(
                        1L,
                        "Senouis",
                        List.of(QueryItemResponse.builder().title("10회 편집 달성").description("축하합니다! 10회 편집 횟수를 달성했습니다!").progress(10).maxprogress(10).build(),
                                QueryItemResponse.builder().title("100회 편집 달성").description("축하합니다! 100회 편집 횟수를 달성했습니다!").progress(100).maxprogress(100).build(),
                                QueryItemResponse.builder().title("1000회 편집 달성").description("축하합니다! 1000회 편집 횟수를 달성했습니다!").progress(1000).maxprogress(1000).build(),
                                QueryItemResponse.builder().title("10000회 편집 달성").description("어떻게 여기까지 온 거죠? 10000회 편집 횟수를 달성했습니다!").progress(10000).maxprogress(10000).build(),
                                QueryItemResponse.builder().title("42회 편집 달성").description("모든 것의 답안이 되는 42회 편집 횟수를 달성했습니다!").progress(42).maxprogress(42).build(),
                                QueryItemResponse.builder().title("300회 편집 달성").description("This is SPARTAAAAA!!! 300회 편집 횟수를 달성했습니다!").progress(300).maxprogress(300).build(),
                                QueryItemResponse.builder().title("500회 편집 달성").description("축하합니다! 500회 편집 횟수를 달성했습니다!").progress(500).maxprogress(500).build(),
                                QueryItemResponse.builder().title("5000회 편집 달성").description("어떻게 여기까지 온 거죠? 5000회 편집 횟수를 달성했습니다!").progress(5000).maxprogress(5000).build()
                        )
                )
        );

        // PUT Test: edit 관련 도전과제로 테스트
        ResponseEntity<List<AchievementResponse>> putResult = achievementController.editAchievement(UserInfo.builder().username("Senouis").role("BASIC").build(), editAchievementRequest);
        assertThat(putResult.getStatusCode().value()).isEqualTo(200);
        assertThat(putResult.getBody().size()).isEqualTo(8);
        // GET Test: 개발자 이름으로 조회하여 테스트
        ResponseEntity<QueryResponse> getResult = achievementController.getAchievements("Senouis");
        assertThat(getResult.getStatusCode().value()).isEqualTo(200);
        assertThat(getResult.getBody().getList().size()).isEqualTo(8);

    }
}
