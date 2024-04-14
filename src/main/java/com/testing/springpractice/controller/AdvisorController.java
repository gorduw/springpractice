package com.testing.springpractice.controller;

import com.testing.springpractice.exception.NotFoundException;
import com.testing.springpractice.repository.entity.AdvisorEntity;
import com.testing.springpractice.repository.entity.PortfolioEntity;
import com.testing.springpractice.repository.AdvisorRepository;
import com.testing.springpractice.service.AdvisorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/advisors")
public class AdvisorController {

    private AdvisorRepository advisorRepository;

    @Autowired
    private AdvisorService advisorService;

    public AdvisorController(AdvisorRepository advisorRepository) {
        this.advisorRepository = advisorRepository;
    }

    @GetMapping
    public String getAllAdvisorsPage() {
        return "main_web_page";
    }

    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity<List<AdvisorEntity>> getAllAdvisorsData() {
        try {
            List<AdvisorEntity> advisorEntities = (List<AdvisorEntity>) advisorRepository.findAll();
            return ResponseEntity.status(HttpStatus.OK).body(advisorEntities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/name/{advisorId}")
    @ResponseBody
    public ResponseEntity<String> getAdvisorName(@PathVariable Long advisorId) {
        String advisorName = advisorRepository.findById(advisorId)
                .map(AdvisorEntity::getName)
                .orElse("Advisor Not Found");
        return ResponseEntity.ok(advisorName);
    }

    @GetMapping("/create")
    public String getCreatePage() {
        return "create_advisor_page";
    }

    @PostMapping(value = "/create", consumes = "application/json")
    @ResponseBody
    public ResponseEntity addAdvisor(@RequestBody AdvisorEntity advisorEntity) {
        AdvisorEntity newAdvisorEntity = advisorRepository.save(advisorEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAdvisorEntity);
    }

    @PostMapping("/create")
    @ResponseBody
    public ModelAndView addAdvisorForm(@RequestParam String name, @RequestParam int age) {
        // Process the form data and create the advisor
        AdvisorEntity newAdvisorEntity = new AdvisorEntity();
        newAdvisorEntity.setAge(age);
        newAdvisorEntity.setName(name);

        advisorRepository.save(newAdvisorEntity);

        return new ModelAndView("redirect:/advisors?success=true");
    }


    @GetMapping("/edit/{id}")
    public String getEditAdvisorPage(Model model, @PathVariable Long id) {
        Optional<AdvisorEntity> advisor = advisorRepository.findById(id);
        model.addAttribute("advisorEdit", advisor.get());
        return "edit_advisor_page";
    }

    @PutMapping(value = "/edit/{id}", consumes = "application/json")
    @ResponseBody
    public ResponseEntity editAdvisor(@RequestBody AdvisorEntity updatedAdvisorEntity, @PathVariable Long id) {
        Optional<AdvisorEntity> advisorOptional = advisorRepository.findById(id);
        AdvisorEntity advisorEntity = advisorOptional.get();
        advisorEntity.setName(updatedAdvisorEntity.getName());
        advisorEntity.setAge(updatedAdvisorEntity.getAge());
        advisorRepository.save(advisorEntity);
        return ResponseEntity.status(HttpStatus.OK).body(advisorEntity);
    }

    @PutMapping("/edit/{id}")
    @ResponseBody
    public ModelAndView editAdvisorForm(@RequestParam String name, @RequestParam int age, @PathVariable Long id) {
        Optional<AdvisorEntity> advisorOptional = advisorRepository.findById(id);
        AdvisorEntity advisorEntity = advisorOptional.get();
        advisorEntity.setAge(age);
        advisorEntity.setName(name);

        advisorRepository.save(advisorEntity);
        return new ModelAndView("redirect:/advisors");
    }


    @DeleteMapping("/delete")
    @ResponseBody
    public ResponseEntity<String> deleteAdvisor(@RequestParam(value = "id") Long id) {
        AdvisorEntity advisor = advisorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Advisor not found"));

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
