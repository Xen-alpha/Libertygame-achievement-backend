package org.libertymedia.libertyachievement.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SignupRequest {
    private String username;
    private String password;

    public UserEntity toEntity() {
        return UserEntity.builder().username(username).password(password).build();
    }
}
