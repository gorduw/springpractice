package com.testing.springpractice.controller;

import com.testing.springpractice.model.Advisor;
import com.testing.springpractice.model.Portfolio;
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
    public ResponseEntity<List<Advisor>> getAllAdvisorsData() {
        try {
            List<Advisor> advisors = (List<Advisor>) advisorRepository.findAll();
            return ResponseEntity.status(HttpStatus.OK).body(advisors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/name/{advisorId}")
    public ResponseEntity<String> getAdvisorName(@PathVariable Long advisorId) {
        String advisorName = advisorRepository.findById(advisorId)
                .map(Advisor::getName)
                .orElse("Advisor Not Found");
        return ResponseEntity.ok(advisorName);
    }

    @GetMapping("/create")
    public String getCreatePage() {
        return "create_advisor_page";
    }

    @PostMapping(value = "/create", consumes = "application/json")
    public ResponseEntity addAdvisor(@RequestBody Advisor advisor) {
        Advisor newAdvisor = advisorRepository.save(advisor);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAdvisor);
    }

    @PostMapping("/create")
    public ModelAndView addAdvisorForm(@RequestParam String name, @RequestParam int age) {
        // Process the form data and create the advisor
        Advisor newAdvisor = new Advisor();
        newAdvisor.setAge(age);
        newAdvisor.setName(name);

        advisorRepository.save(newAdvisor);

        return new ModelAndView("redirect:/advisors?success=true");
    }


    @GetMapping("/edit/{id}")
    public String getEditAdvisorPage(Model model, @PathVariable Long id) {
        Optional<Advisor> advisor = advisorRepository.findById(id);
        model.addAttribute("advisorEdit", advisor.get());
        return "edit_advisor_page";
    }

    @PutMapping(value = "/edit/{id}", consumes = "application/json")
    public ResponseEntity editAdvisor(@RequestBody Advisor updatedAdvisor, @PathVariable Long id) {
        Optional<Advisor> advisorOptional = advisorRepository.findById(id);
        Advisor advisor = advisorOptional.get();
        advisor.setName(updatedAdvisor.getName());
        advisor.setAge(updatedAdvisor.getAge());
        advisorRepository.save(advisor);
        return ResponseEntity.status(HttpStatus.OK).body(advisor);
    }

    @PutMapping("/edit/{id}")
    public ModelAndView editAdvisorForm(@RequestParam String name, @RequestParam int age, @PathVariable Long id) {
        Optional<Advisor> advisorOptional = advisorRepository.findById(id);
        Advisor advisor = advisorOptional.get();
        advisor.setAge(age);
        advisor.setName(name);

        advisorRepository.save(advisor);
        return new ModelAndView("redirect:/advisors");
    }


    @DeleteMapping("/delete")
    public ResponseEntity deleteAdvisor(@RequestParam(value = "id") Long id) {
        try {
            Optional<Advisor> advisorOptional = advisorRepository.findById(id);
            if (advisorOptional.isPresent()) {
                advisorRepository.deleteById(id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Advisor deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Advisor not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete advisor: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/portfolios/page")
    public String getAdvisorsPortfoliosPage(Model model, @PathVariable Long id) {
        model.addAttribute("advisorId", id);
        return "portfolio_page";
    }

    @GetMapping("/{id}/portfolios/data")
    public ResponseEntity getAdvisorPortfolios(@PathVariable Long id) {
        try {
            List<Portfolio> portfolios = advisorService.getAdvisorPortfolios(id);
            return ResponseEntity.ok(portfolios);
        } catch (ResponseStatusException ex) {
            System.out.println(ex.getMessage());
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getMessage());
        }
    }

}
