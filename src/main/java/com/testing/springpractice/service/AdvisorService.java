package com.testing.springpractice.service;


import com.testing.springpractice.dto.AdvisorDTO;
import com.testing.springpractice.exception.NotFoundException;
import com.testing.springpractice.mapper.AdvisorToDtoMapperImpl;
import com.testing.springpractice.repository.AdvisorRepository;
import com.testing.springpractice.repository.PortfolioRepository;
import com.testing.springpractice.repository.entity.AdvisorEntity;
import com.testing.springpractice.repository.model.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdvisorService implements UserDetailsService {

    private final AdvisorRepository advisorRepository;
    private final PortfolioRepository portfolioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdvisorService(AdvisorRepository advisorRepository, PortfolioRepository portfolioRepository, PasswordEncoder passwordEncoder) {
        this.advisorRepository = advisorRepository;
        this.portfolioRepository = portfolioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AdvisorDTO> getAllAdvisorDto() {
        List<AdvisorEntity> advisorEntities = new ArrayList<>();
        advisorRepository.findAll().forEach(advisorEntities::add);

        List<AdvisorDTO> advisorDTOs = advisorEntities.stream()
                .map(advisor -> AdvisorToDtoMapperImpl.INSTANCE.advisorToAdvisorDTO(advisor))
                .collect(Collectors.toList());

        return advisorDTOs;
    }

    public AdvisorDTO findAdvisorById(final Long id) {
        return advisorRepository.findById(id)
                .map(AdvisorToDtoMapperImpl.INSTANCE::advisorToAdvisorDTO)
                .orElseThrow(() -> new NotFoundException("Advisor", "ID", id.toString()));
    }

    public AdvisorDTO postAdvisorDto(final AdvisorDTO advisorDTO) {
        advisorDTO.setPassword(passwordEncoder.encode(advisorDTO.getPassword()));
        AdvisorEntity advisorEntity = AdvisorToDtoMapperImpl.INSTANCE.advisorDtoToAdvisor(advisorDTO);
        advisorEntity.setEnabled(true);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails currentUser = (CustomUserDetails) authentication.getPrincipal();
            if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER")) || currentUser.getManagerId() == null) {
                advisorEntity.setManagerId(currentUser.getId());
            }
        }

        advisorEntity = advisorRepository.save(advisorEntity);
        return AdvisorToDtoMapperImpl.INSTANCE.advisorToAdvisorDTO(advisorEntity);
    }

    public AdvisorDTO updateAdvisor(final AdvisorDTO advisorDTO) {
        AdvisorEntity existingAdvisorEntity = advisorRepository.findById(advisorDTO.getAdvisorId())
                .orElseThrow(() -> new NotFoundException("Advisor", "ID", advisorDTO.getAdvisorId().toString()));

        existingAdvisorEntity.setName(advisorDTO.getName());
        existingAdvisorEntity.setAge(advisorDTO.getAge());
        existingAdvisorEntity.setEmail(advisorDTO.getEmail());

        AdvisorEntity updatedAdvisorEntity = advisorRepository.save(existingAdvisorEntity);

        return AdvisorToDtoMapperImpl.INSTANCE.advisorToAdvisorDTO(updatedAdvisorEntity);
    }

    public boolean isRequiredAdvisorLogged(final Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            return false;
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId().equals(id);
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        AdvisorEntity advisorEntity = advisorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + email));

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (advisorEntity.isEnabled() && advisorEntity.getManagerId() == null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_MANAGER"));
        }

        return new CustomUserDetails(
                advisorEntity.getEmail(),
                advisorEntity.getPassword(),
                authorities,
                advisorEntity.getId(),
                advisorEntity.isEnabled(),
                advisorEntity.getManagerId()
        );
    }
}
