package com.testing.springpractice.controller;

import com.testing.springpractice.dto.AdvisorDTO;
import com.testing.springpractice.mapper.AdvisorToDtoMapperImpl;
import com.testing.springpractice.repository.AdvisorRepository;
import com.testing.springpractice.repository.entity.PortfolioEntity;
import com.testing.springpractice.service.AdvisorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;


@Controller
@RequestMapping("/advisors")
public class AdvisorController {

    private AdvisorRepository advisorRepository;

    private AdvisorService advisorService;

    public AdvisorController(AdvisorRepository advisorRepository, AdvisorService advisorService) {
        this.advisorRepository = advisorRepository;
        this.advisorService = advisorService;
    }

    @GetMapping
    public String getAllAdvisorsPage() {
        return "main_web_page";
    }

    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity<List<AdvisorDTO>> getAllAdvisorsData() {
        return ResponseEntity.status(HttpStatus.OK).body(advisorService.getAllAdvisorDto());
    }

    @GetMapping("/name/{advisorId}")
    @ResponseBody
    public ResponseEntity<String> getAdvisorName(@PathVariable Long advisorId) {
        String advisorName = advisorService.findAdvisorById(advisorId).getName();
        return ResponseEntity.ok(advisorName);
    }

    @GetMapping("/create")
    public String getCreatePage() {
        return "create_advisor_page";
    }

    @PostMapping(value = "/create", consumes = "application/json")
    @ResponseBody
    public ResponseEntity addAdvisor(@RequestBody AdvisorDTO advisorDTO) {

        return ResponseEntity.status(HttpStatus.CREATED).body(advisorService.postAdvisorDto(advisorDTO));
    }

    @PostMapping("/create")
    @ResponseBody
    public ModelAndView addAdvisorForm(@RequestParam String name, @RequestParam int age) {
        // Process the form data and create the advisor
        AdvisorDTO newAdvisorDto = new AdvisorDTO();
        newAdvisorDto.setAge(age);
        newAdvisorDto.setName(name);

        advisorRepository.save(AdvisorToDtoMapperImpl.INSTANCE.advisorDtoToAdvisor(newAdvisorDto));

        return new ModelAndView("redirect:/advisors?success=true");
    }


    @GetMapping("/edit/{id}")
    public String getEditAdvisorPage(Model model, @PathVariable Long id) {
        AdvisorDTO advisorDTO = advisorService.findAdvisorById(id);
        model.addAttribute("advisorEdit", advisorDTO);
        return "edit_advisor_page";
    }

    @PutMapping(value = "/edit/{id}", consumes = "application/json")
    @ResponseBody
    public ResponseEntity editAdvisor(@RequestBody AdvisorDTO updatedAdvisorDto, @PathVariable Long id) {
        AdvisorDTO advisorDTO = advisorService.findAdvisorById(id);
        advisorDTO.setName(updatedAdvisorDto.getName());
        advisorDTO.setAge(updatedAdvisorDto.getAge());
        advisorService.updateAdvisor(advisorDTO);
        return ResponseEntity.status(HttpStatus.OK).body(advisorDTO);
    }

    @PutMapping("/edit/{id}")
    @ResponseBody
    public ModelAndView editAdvisorForm(@RequestParam String name, @RequestParam int age, @PathVariable Long id) {
        AdvisorDTO advisorDTO = advisorService.findAdvisorById(id);
        advisorDTO.setName(name);
        advisorDTO.setAge(age);
        advisorService.updateAdvisor(advisorDTO);
        return new ModelAndView("redirect:/advisors");
    }


    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteAdvisor(@RequestParam(value = "id") Long id) {
        AdvisorDTO advisorDTO = advisorService.findAdvisorById(id);

        advisorRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Advisor deleted successfully");
    }

    @GetMapping("/{id}/portfolios/page")
    public String getAdvisorsPortfoliosPage(Model model, @PathVariable Long id) {
        model.addAttribute("advisorId", id);
        return "portfolio_page";
    }

    @GetMapping("/{id}/portfolios/data")
    @ResponseBody
    public ResponseEntity getAdvisorPortfolios(@PathVariable Long id) {
        try {
            List<PortfolioEntity> portfolioEntities = advisorService.getAdvisorPortfolios(id);
            return ResponseEntity.ok(portfolioEntities);
        } catch (ResponseStatusException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
        }
    }

}
