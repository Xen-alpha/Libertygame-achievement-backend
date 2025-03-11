package org.libertymedia.libertyachievement.user.model;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Getter
public class LibertyOAuth2User implements OAuth2User {
    private UserInfo user;

    public LibertyOAuth2User(UserInfo user) {
        this.user = user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of("username", user.getUsername());
    }

    @Override
    public String getName() {
        return user.getUsername();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());

        authorities.add(authority);
        return authorities;
    }



}
