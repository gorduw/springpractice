package com.testing.springpractice.controller;

import com.testing.springpractice.model.Advisor;
import com.testing.springpractice.repository.AdvisorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/advisors")
@ComponentScan(basePackages = "com.testing.sombra.repository")
public class AdvisorController {

    private AdvisorRepository advisorRepository;

    @Autowired
    public AdvisorController(@Qualifier("advisorRepository") AdvisorRepository advisorRepository) {
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


    @GetMapping("/create")
    public String getCreatePage() {
        return "create_advisor_page";
    }

    @PostMapping("/create")
    public ResponseEntity addAdvisor(@RequestBody Advisor advisor) {
        Advisor newAdvisor = advisorRepository.save(advisor);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAdvisor);
    }


    @GetMapping("/edit/{id}")
    public String getEditAdvisorPage(Model model, @PathVariable Long id) {
        Optional<Advisor> advisor = advisorRepository.findById(id);
        model.addAttribute("advisorEdit", advisor.get());
        return "edit_advisor_page";

    }

    @PutMapping("/edit/{id}")
    public ResponseEntity editAdvisor(@RequestBody Advisor updatedAdvisor, @PathVariable Long id) {
        Optional<Advisor> advisorOptional = advisorRepository.findById(id);
        Advisor advisor = advisorOptional.get();
        advisor.setName(updatedAdvisor.getName());
        advisor.setAge(updatedAdvisor.getAge());
        advisorRepository.save(advisor);
        return ResponseEntity.status(HttpStatus.OK).body(advisor);
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


}
