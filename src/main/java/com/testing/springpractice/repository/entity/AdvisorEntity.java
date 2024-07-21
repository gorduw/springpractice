package com.testing.springpractice.repository.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

@Entity
@Data
@Table(name = "advisor")
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AdvisorEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    @Column
    @JsonIgnore
    private boolean enabled;

    @Column
    private Long managerId;


    public AdvisorEntity createAdvisorEntityForCustomOauth2User(Map<String, Object> attributes, PasswordEncoder passwordEncoder, String defaultName, String defaultPassword, Integer age) {
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        AdvisorEntity advisorEntity = new AdvisorEntity();
        advisorEntity.setEmail(email);
        advisorEntity.setName(name != null ? name : defaultName);
        advisorEntity.setAge(age);
        advisorEntity.setPassword(passwordEncoder.encode(defaultPassword));
        advisorEntity.setEnabled(true);
        advisorEntity.setManagerId(null);
        advisorEntity.setCreatedBy(1L);
        return advisorEntity;
    }
}
