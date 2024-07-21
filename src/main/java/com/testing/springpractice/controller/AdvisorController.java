package com.testing.springpractice.controller;

import com.testing.springpractice.dto.AdvisorDTO;
import com.testing.springpractice.dto.PortfolioDTO;
import com.testing.springpractice.repository.AdvisorRepository;
import com.testing.springpractice.service.AdvisorService;
import com.testing.springpractice.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/advisors")
@Tag(name = "Advisor Management", description = "Controller for managing advisors")
public class AdvisorController {

    private AdvisorRepository advisorRepository;

    private AdvisorService advisorService;

    private PortfolioService portfolioService;

    public AdvisorController(AdvisorRepository advisorRepository, AdvisorService advisorService, PortfolioService portfolioService) {
        this.advisorRepository = advisorRepository;
        this.advisorService = advisorService;
        this.portfolioService = portfolioService;
    }

    @GetMapping("/data")
    @ResponseBody
    @Operation(summary = "List all advisors", description = "Retrieve all advisors in the system as data transfer objects.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
    public ResponseEntity<List<AdvisorDTO>> getAllAdvisorsData() {
        return ResponseEntity.status(HttpStatus.OK).body(advisorService.getAllAdvisorDto());
    }

    @GetMapping("/name/{id}")
    @ResponseBody
    @Operation(summary = "Get advisor name", description = "Fetch the name of a specific advisor by their ID.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved name")
    public ResponseEntity<String> getAdvisorName(final @PathVariable Long id) {
        String advisorName = advisorService.findAdvisorById(id).getName();
        return ResponseEntity.ok(advisorName);
    }


    @PostMapping(value = "/create", consumes = "application/json")
    @ResponseBody
    @Operation(summary = "Create an advisor", description = "Create a new advisor with details provided in JSON format.")
    @ApiResponse(responseCode = "201", description = "Advisor created successfully")
    public ResponseEntity addAdvisor(final @RequestBody AdvisorDTO advisorDTO) {

        return ResponseEntity.status(HttpStatus.CREATED).body(advisorService.postAdvisorDto(advisorDTO));
    }

    @PostMapping("/create")
    @Operation(summary = "Create advisor via form", description = "Create a new advisor using form data.")
    @ApiResponses({
            @ApiResponse(responseCode = "303", description = "Advisor created successfully and redirected", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ModelAndView addAdvisorForm(
            @Parameter(description = "Name of the advisor", required = true) @RequestParam String name,
            @Parameter(description = "Age of the advisor", required = true) @RequestParam int age,
            @Parameter(description = "Email of the advisor", required = true) @RequestParam String email,
            @Parameter(description = "Password of the advisor", required = true) @RequestParam String password) {
        AdvisorDTO newAdvisorDto = new AdvisorDTO();
        newAdvisorDto.setName(name);
        newAdvisorDto.setAge(age);
        newAdvisorDto.setEmail(email);
        newAdvisorDto.setPassword(password); // This password will be encoded in the service layer
        advisorService.postAdvisorDto(newAdvisorDto);
        return new ModelAndView("redirect:/advisors?success=true");
    }


    @PutMapping(value = "/edit/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update advisor details", description = "Update an advisor's details via JSON input.")
    @ApiResponse(responseCode = "200", description = "Advisor updated successfully")
    public ResponseEntity<AdvisorDTO> editAdvisor(
            final @Parameter(description = "Updated advisor DTO", required = true) @RequestBody AdvisorDTO updatedAdvisorDto,
            final @Parameter(description = "ID of the advisor to update", required = true) @PathVariable Long id) {
        AdvisorDTO advisorDTO = advisorService.findAdvisorById(id);
        advisorDTO.setName(updatedAdvisorDto.getName());
        advisorDTO.setAge(updatedAdvisorDto.getAge());
        advisorService.updateAdvisor(advisorDTO);
        return ResponseEntity.ok(advisorDTO);
    }

    @PutMapping("/edit/{id}")
    @Operation(summary = "Update advisor details form", description = "Update an advisor's details via form input.")
    @ApiResponse(responseCode = "200", description = "Advisor updated successfully")
    public ModelAndView editAdvisorForm(final @RequestParam String name, final @RequestParam int age, final @PathVariable Long id) {
        AdvisorDTO advisorDTO = advisorService.findAdvisorById(id);
        advisorDTO.setName(name);
        advisorDTO.setAge(age);
        advisorService.updateAdvisor(advisorDTO);
        return new ModelAndView("redirect:/advisors");
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete an advisor", description = "Delete an advisor based on ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Advisor deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Advisor not found")
    })
    public ResponseEntity<String> deleteAdvisor(
            final @Parameter(description = "ID of the advisor to delete", required = true) @RequestParam Long id) {
        advisorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/portfolios/data")
    @Operation(summary = "Get advisor portfolios data", description = "Retrieve data for all portfolios managed by a specific advisor.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved portfolios")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<List<PortfolioDTO>> getAdvisorPortfolios(
            @Parameter(description = "ID of the advisor to retrieve portfolios for", required = true)
            @PathVariable Long id) {
        List<PortfolioDTO> portfolioEntities = portfolioService.getAdvisorPortfolios(id);
        return ResponseEntity.ok(portfolioEntities);
    }
}
