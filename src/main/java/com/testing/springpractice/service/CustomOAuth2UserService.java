package com.testing.springpractice.service;

import com.testing.springpractice.repository.AdvisorRepository;
import com.testing.springpractice.repository.entity.AdvisorEntity;
import com.testing.springpractice.repository.model.CustomOauth2User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AdvisorRepository advisorRepository;
    private final PasswordEncoder passwordEncoder;
    private final Integer age = 0;
    private final String defaultPassword = "defaultPassword";
    private final String defaultName = "Default Name";

    @PersistenceContext
    private EntityManager entityManager;

    public CustomOAuth2UserService(AdvisorRepository advisorRepository, PasswordEncoder passwordEncoder) {
        this.advisorRepository = advisorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        Optional<AdvisorEntity> advisorOpt = advisorRepository.findByEmail(email);
        AdvisorEntity advisorEntity;
        if (advisorOpt.isEmpty()) {
            advisorEntity = new AdvisorEntity();
            advisorEntity.createAdvisorEntityForCustomOauth2User(attributes, passwordEncoder, defaultName, defaultPassword, age);

            advisorEntity = advisorRepository.save(advisorEntity);
            System.out.println("New advisor saved: " + advisorEntity);

            //TODO sql update (remove @CreatedBy from auditable to check if it blocks)
            advisorEntity.setCreatedBy(advisorEntity.getId());
            advisorRepository.save(advisorEntity); // This save will commit the createdBy change
            System.out.println("Updated advisor with createdBy: " + advisorEntity);
        } else {
            advisorEntity = advisorOpt.get();
        }


        return createOauth2UserFromEntity(advisorEntity, attributes);
    }


    public CustomOauth2User createOauth2UserFromEntity(AdvisorEntity advisorEntity, Map<String, Object> attribute) {
        return new  CustomOauth2User(
                advisorEntity.getEmail(),
                advisorEntity.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")),
                advisorEntity.getId(),
                advisorEntity.isEnabled(),
                advisorEntity.getManagerId(),
                attribute
        );
    }
}
