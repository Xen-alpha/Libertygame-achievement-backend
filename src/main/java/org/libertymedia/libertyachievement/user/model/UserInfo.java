package org.libertymedia.libertyachievement.user.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.libertymedia.libertyachievement.achievement.model.Progress;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Schema(description="사용자 정보")
public class UserInfo implements UserDetails {
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.notBlocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_BASIC");
        authorities.add(authority);
        if (role.equals("ADVANCED")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADVANCED"));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    @Column(unique = true, nullable = false)
    private Long userIdx;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String password; // 귀찮게도 추후의 자체 회원 시스템을 대비하려면 UserDetails 상속과 함께 이것을 남겨야 할 것이다. 지금은 액세스 토큰 임시 보관용

    private String email;
    @Column(nullable = false)
    private String role;

    private Boolean notBlocked;

    @OneToOne
    private Promotion promotion;

    @OneToMany(mappedBy = "user")
    private List<Progress> progressList;

}
