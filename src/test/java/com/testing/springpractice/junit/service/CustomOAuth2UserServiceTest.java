package com.testing.springpractice.junit.service;

import com.testing.springpractice.repository.AdvisorRepository;
import com.testing.springpractice.repository.entity.AdvisorEntity;
import com.testing.springpractice.repository.model.CustomOauth2User;
import com.testing.springpractice.service.CustomOAuth2UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import jakarta.persistence.EntityManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CustomOAuth2UserServiceTest {

    @Mock
    private AdvisorRepository advisorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    @Mock
    private OAuth2UserRequest oAuth2UserRequest;

    @Captor
    private ArgumentCaptor<AdvisorEntity> advisorCaptor;

    @Mock
    private ClientRegistration clientRegistration;

    @Mock
    private ClientRegistration.ProviderDetails providerDetails;

    @Mock
    private ClientRegistration.ProviderDetails.UserInfoEndpoint userInfoEndpoint;

    @Mock
    private DefaultOAuth2UserService defaultOAuth2UserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock the ClientRegistration and its nested objects
        when(oAuth2UserRequest.getClientRegistration()).thenReturn(clientRegistration);
        when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
        when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);
        when(userInfoEndpoint.getUri()).thenReturn("https://example.com/userinfo");
        when(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()).thenReturn("email");

        // Mock the OAuth2AccessToken
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "access-token-value", null, null);
        when(oAuth2UserRequest.getAccessToken()).thenReturn(accessToken);

        // Inject the mocked DefaultOAuth2UserService into the custom service
        customOAuth2UserService = new CustomOAuth2UserService(advisorRepository, passwordEncoder) {
            @Override
            public OAuth2User loadUser(OAuth2UserRequest userRequest) {
                return defaultOAuth2UserService.loadUser(userRequest);
            }
        };
    }

    @Test
    void testLoadUser_NewAdvisor() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "newuser@example.com");
        attributes.put("name", "New User");

        OAuth2User oAuth2User = new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), attributes, "email");
        when(defaultOAuth2UserService.loadUser(oAuth2UserRequest)).thenReturn(oAuth2User);

        when(advisorRepository.findByEmail("newuser@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(advisorRepository.save(any(AdvisorEntity.class))).thenAnswer(invocation -> {
            AdvisorEntity entity = invocation.getArgument(0);
            entity.setId(1L); // simulate setting the ID after saving
            return entity;
        });

        OAuth2User result = customOAuth2UserService.loadUser(oAuth2UserRequest);

        verify(advisorRepository, times(2)).save(advisorCaptor.capture());
        AdvisorEntity savedAdvisor = advisorCaptor.getAllValues().get(0);
        AdvisorEntity updatedAdvisor = advisorCaptor.getAllValues().get(1);

        assertNotNull(result);
        assertTrue(result instanceof CustomOauth2User);
        assertEquals("newuser@example.com", result.getName());
        assertEquals("New User", savedAdvisor.getName());
        assertEquals("encodedPassword", savedAdvisor.getPassword());
        assertTrue(savedAdvisor.isEnabled());
        assertNull(savedAdvisor.getManagerId());
        assertEquals(savedAdvisor.getId(), updatedAdvisor.getCreatedBy());
    }

    @Test
    void testLoadUser_ExistingAdvisor() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "existinguser@example.com");
        attributes.put("name", "Existing User");

        OAuth2User oAuth2User = new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), attributes, "email");
        when(defaultOAuth2UserService.loadUser(oAuth2UserRequest)).thenReturn(oAuth2User);

        AdvisorEntity advisorEntity = new AdvisorEntity();
        advisorEntity.setId(1L);
        advisorEntity.setEmail("existinguser@example.com");
        advisorEntity.setName("Existing User");
        advisorEntity.setPassword("password");
        advisorEntity.setEnabled(true);
        advisorEntity.setManagerId(null);

        when(advisorRepository.findByEmail("existinguser@example.com")).thenReturn(Optional.of(advisorEntity));

        OAuth2User result = customOAuth2UserService.loadUser(oAuth2UserRequest);

        verify(advisorRepository, never()).save(any(AdvisorEntity.class));

        assertNotNull(result);
        assertTrue(result instanceof CustomOauth2User);
        assertEquals("existinguser@example.com", result.getName());
    }

    @Test
    void testLoadUser_NullEmail() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", null);
        attributes.put("name", "User with Null Email");

        OAuth2User oAuth2User = new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), attributes, "email");
        when(defaultOAuth2UserService.loadUser(oAuth2UserRequest)).thenReturn(oAuth2User);

        assertThrows(NullPointerException.class, () -> customOAuth2UserService.loadUser(oAuth2UserRequest));
    }

    @Test
    void testLoadUser_NoName() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "user@example.com");

        OAuth2User oAuth2User = new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), attributes, "email");
        when(defaultOAuth2UserService.loadUser(oAuth2UserRequest)).thenReturn(oAuth2User);

        when(advisorRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(advisorRepository.save(any(AdvisorEntity.class))).thenAnswer(invocation -> {
            AdvisorEntity entity = invocation.getArgument(0);
            entity.setId(1L); // simulate setting the ID after saving
            return entity;
        });

        OAuth2User result = customOAuth2UserService.loadUser(oAuth2UserRequest);

        verify(advisorRepository, times(2)).save(advisorCaptor.capture());
        AdvisorEntity savedAdvisor = advisorCaptor.getAllValues().get(0);
        AdvisorEntity updatedAdvisor = advisorCaptor.getAllValues().get(1);

        assertNotNull(result);
        assertTrue(result instanceof CustomOauth2User);
        assertEquals("user@example.com", result.getName());
        assertEquals("Default Name", savedAdvisor.getName());
        assertEquals("encodedPassword", savedAdvisor.getPassword());
        assertTrue(savedAdvisor.isEnabled());
        assertNull(savedAdvisor.getManagerId());
        assertEquals(savedAdvisor.getId(), updatedAdvisor.getCreatedBy());
    }

    @Test
    void testLoadUser_ExistingAdvisorWithManagerId() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("email", "manager@example.com");
        attributes.put("name", "Manager User");

        OAuth2User oAuth2User = new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")), attributes, "email");
        when(defaultOAuth2UserService.loadUser(oAuth2UserRequest)).thenReturn(oAuth2User);

        AdvisorEntity advisorEntity = new AdvisorEntity();
        advisorEntity.setId(1L);
        advisorEntity.setEmail("manager@example.com");
        advisorEntity.setName("Manager User");
        advisorEntity.setPassword("password");
        advisorEntity.setEnabled(true);
        advisorEntity.setManagerId(2L);

        when(advisorRepository.findByEmail("manager@example.com")).thenReturn(Optional.of(advisorEntity));

        OAuth2User result = customOAuth2UserService.loadUser(oAuth2UserRequest);

        verify(advisorRepository, never()).save(any(AdvisorEntity.class));

        assertNotNull(result);
        assertTrue(result instanceof CustomOauth2User);
        assertEquals("manager@example.com", result.getName());
        assertEquals(2L, ((CustomOauth2User) result).getManagerId());
    }
}
