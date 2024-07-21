package com.testing.springpractice.junit.service;

import com.testing.springpractice.dto.AdvisorDTO;
import com.testing.springpractice.exception.NotFoundException;
import com.testing.springpractice.repository.AdvisorRepository;
import com.testing.springpractice.repository.entity.AdvisorEntity;
import com.testing.springpractice.repository.model.CustomUserDetails;
import com.testing.springpractice.service.AdvisorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AdvisorServiceTests {

    @Mock
    private AdvisorRepository advisorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AdvisorService advisorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetAllAdvisorDto() {
        AdvisorEntity advisorEntity = createTestAdvisorEntity();
        when(advisorRepository.findAll()).thenReturn(Collections.singletonList(advisorEntity));

        List<AdvisorDTO> advisorDTOs = advisorService.getAllAdvisorDto();

        assertNotNull(advisorDTOs);
        assertEquals(1, advisorDTOs.size());
        assertEquals(advisorEntity.getName(), advisorDTOs.get(0).getName());
    }

    @Test
    void testFindAdvisorById() {
        AdvisorEntity advisorEntity = createTestAdvisorEntity();
        when(advisorRepository.findById(any(Long.class))).thenReturn(Optional.of(advisorEntity));

        AdvisorDTO advisorDTO = advisorService.findAdvisorById(1L);

        assertNotNull(advisorDTO);
        assertEquals(advisorEntity.getName(), advisorDTO.getName());
    }

    @Test
    void testFindAdvisorByIdNotFound() {
        when(advisorRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> advisorService.findAdvisorById(1L));

        assertEquals("Advisor not found with ID: 1", thrown.getMessage());
    }

    @Test
    void testPostAdvisorDto() {
        AdvisorDTO advisorDTO = createTestAdvisorDTO();
        AdvisorEntity advisorEntity = createTestAdvisorEntity();

        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(advisorRepository.save(any(AdvisorEntity.class))).thenReturn(advisorEntity);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new CustomUserDetails("email", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")), 1L, true, null));

        AdvisorDTO result = advisorService.postAdvisorDto(advisorDTO);

        assertNotNull(result);
        assertEquals(advisorDTO.getName(), result.getName());
    }

    @Test
    void testUpdateAdvisor() {
        AdvisorDTO advisorDTO = createTestAdvisorDTO();
        AdvisorEntity advisorEntity = createTestAdvisorEntity();

        when(advisorRepository.findById(any(Long.class))).thenReturn(Optional.of(advisorEntity));
        when(advisorRepository.save(any(AdvisorEntity.class))).thenReturn(advisorEntity);

        AdvisorDTO result = advisorService.updateAdvisor(advisorDTO);

        assertNotNull(result);
        assertEquals(advisorDTO.getName(), result.getName());
    }

    @Test
    void testUpdateAdvisorNotFound() {
        when(advisorRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        NotFoundException thrown = assertThrows(NotFoundException.class, () -> advisorService.updateAdvisor(createTestAdvisorDTO()));

        assertEquals("Advisor not found with ID: 1", thrown.getMessage());
    }

    @Test
    void testIsRequiredAdvisorLogged() {
        CustomUserDetails userDetails = new CustomUserDetails("email", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")), 1L, true, null);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        boolean result = advisorService.isRequiredAdvisorLogged(1L);

        assertTrue(result);
    }

    @Test
    void testIsRequiredAdvisorLoggedFalse() {
        CustomUserDetails userDetails = new CustomUserDetails("email", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")), 1L, true, null);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        boolean result = advisorService.isRequiredAdvisorLogged(2L);

        assertFalse(result);
    }

    @Test
    void testLoadUserByUsername() {
        AdvisorEntity advisorEntity = createTestAdvisorEntity();
        when(advisorRepository.findByEmail(any(String.class))).thenReturn(Optional.of(advisorEntity));

        CustomUserDetails userDetails = (CustomUserDetails) advisorService.loadUserByUsername("email");

        assertNotNull(userDetails);
        assertEquals(advisorEntity.getEmail(), userDetails.getUsername());
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(advisorRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> advisorService.loadUserByUsername("email"));

        assertEquals("No user found with email: email", thrown.getMessage());
    }

    private AdvisorEntity createTestAdvisorEntity() {
        AdvisorEntity advisor = new AdvisorEntity();
        advisor.setId(1L);
        advisor.setName("Test Advisor");
        advisor.setEmail("test@advisor.com");
        advisor.setPassword("password");
        advisor.setEnabled(true);
        return advisor;
    }

    private AdvisorDTO createTestAdvisorDTO() {
        AdvisorDTO advisorDTO = new AdvisorDTO();
        advisorDTO.setAdvisorId(1L);
        advisorDTO.setName("Test Advisor");
        advisorDTO.setEmail("test@advisor.com");
        advisorDTO.setPassword("password");
        advisorDTO.setAge(30);
        return advisorDTO;
    }
}
