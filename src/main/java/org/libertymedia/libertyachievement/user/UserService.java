package org.libertymedia.libertyachievement.user;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.libertymedia.libertyachievement.user.model.Promotion;
import org.libertymedia.libertyachievement.user.model.UserInfo;
import org.libertymedia.libertyachievement.user.model.request.PromotionRequest;
import org.libertymedia.libertyachievement.util.JwtUtil;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final PromotionRepository promotionRepository;


    @Transactional
    public String promotionAccepted(Long userIdx) {
        UserInfo targetUser = userRepository.findById(userIdx).orElse(null);
        if (targetUser == null) {
            throw new RuntimeException("User not found");
        }
        // 프로모션 요청 기록 검증
        Promotion prevRequest = promotionRepository.findByUser(targetUser).orElse(null);
        if (prevRequest == null) {
            throw new IllegalStateException("Requested time not found");
        }
        // 실수로 12시간 이전의 날짜를 보내면 그대로 오류 던지기
        ZonedDateTime dueDate = ZonedDateTime.now().minusHours(12);
        ZonedDateTime prev = prevRequest.getRequestDate();
        if ( prev.isBefore(dueDate) ) {
            // 만료된 요청, 삭제
            promotionRepository.delete(prevRequest);
            return "만료된 요청으로 거부됨";
        }
        // 유효하므로 프로모션 처리
        targetUser.setRole("ADVANCED");
        userRepository.save(targetUser);
        return "수락됨";
    }

    @Transactional
    public String promotionRejected(Long userIdx) {
        UserInfo targetUser = userRepository.findById(userIdx).orElse(null);
        if (targetUser == null) {
            throw new RuntimeException("사용자 없음");
        } else {
            userRepository.save(targetUser);
            promotionRepository.delete(targetUser.getPromotion());
        }
        return "거부 처리 완료";
    }

    @Transactional
    public void createPromotion(PromotionRequest request)  {
        UserInfo user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("사용자 없음");
        }
        Promotion promotion = Promotion.builder().user(user).accepted(false).requestDate(ZonedDateTime.now()).build();
        promotionRepository.save(promotion);
        String contents = """
            리버티게임 도전과제 생성을 요청하셨습니다.\n\n
            OAuth2 연동 로그인이 되어 있는지 확인 후 승급을 하려면 아래 링크를 누르세요.\n
            https://dev.libertgame.work:8080/user/promote/accepted/"""+ user.getUserIdx() +"""
            \n\n
            만일 승급을 취소하려면 아래 링크를 누르세요\n
            https://dev.libertgame.work:8080/user/promote/declined/"""+ user.getUserIdx() +"""
            \n리버티게임을 이용해주셔서 감사합니다.
            """;
        // 메일 보내기
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message);
            messageHelper.setSubject("리버티게임 도전과제 제작자 승급 요청을 하셨습니다");
            messageHelper.setTo(request.getEmail());
            messageHelper.setText(contents, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("승급 안내 메일 전송 실패");
        }
    }

    @Transactional
    public String getNewToken(String refreshToken) {
        UserInfo tokenInfo = JwtUtil.getUser(refreshToken);
        String username = tokenInfo.getUsername();
        //  DB/캐시에서 다시 조회하는 것이 안전
        UserInfo userInfo = userRepository.findByUsername(username).orElseThrow();
        if (!userInfo.getUsername().equals(username)) {
            return null;
        }
        return JwtUtil.generateToken(userInfo.getUserIdx(), userInfo.getUsername(), userInfo.getEmail(), userInfo.getRole(), userInfo.getNotBlocked());
    }

    @Transactional
    public void deleteRefreshToken(String userName) {
        UserInfo userInfo = userRepository.findByUsername(userName).orElse(null);
        if (userInfo != null) {
            userInfo.setRefreshToken(null);
            userRepository.save(userInfo);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> result = userRepository.findByUsername(username);
        if (result.isPresent()) {
            // 7번 로직
            UserInfo user = result.get();
            return user;
        }

        return null;
    }
}
