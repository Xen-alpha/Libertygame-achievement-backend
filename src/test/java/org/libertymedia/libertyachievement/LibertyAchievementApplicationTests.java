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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class LibertyAchievementApplicationTests {


}
