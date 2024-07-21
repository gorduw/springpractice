package com.testing.springpractice.repository.model;

import com.testing.springpractice.repository.entity.AdvisorEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOauth2User extends CustomUserDetails implements OAuth2User {

    private final Map<String, Object> attributes;

    public CustomOauth2User(String username, String password, Collection<? extends GrantedAuthority> authorities, Long id, boolean isEnabled, Long managerId, Map<String, Object> attributes) {
        super(username, password, authorities, id, isEnabled, managerId);
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return getUsername();
    }

}