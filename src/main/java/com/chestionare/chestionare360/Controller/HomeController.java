package com.chestionare.chestionare360.Controller;


import com.chestionare.chestionare360.Model.QuizQuestion;
import com.chestionare.chestionare360.Repository.QuizQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final QuizQuestionRepository quizQuestionRepository;

    @GetMapping
    public String homePage(){
        return "home";
    }

    @GetMapping("/donate")
    public String showDonatePage() {
        return "philanthropy";
    }

    @GetMapping("/learning-environment")
    public String learningEnvironment(Model model) {
        List<String> categories = List.of("A", "B", "C", "D", "E", "Tr", "13din15");

        List<String> existingCategories = categories.stream()
                .filter(quizQuestionRepository::existsByCategory)
                .toList();
        model.addAttribute("categories", categories);
        return "learning-environment";
    }

    @GetMapping("/car-quizzes")
    public String carQuizzes(Model model) {
        List<String> categories = List.of("A", "B", "C", "D", "E", "Tr", "13din15");

        List<String> existingCategories = categories.stream()
                .filter(quizQuestionRepository::existsByCategory)
                .toList();
        model.addAttribute("categories", categories);
        return "car-quizzes";
    }

    @GetMapping("/drpciv-questions")
    public String drpcivQuestions() {
        return "drpciv-questions";
    }

    @GetMapping("/report-question")
    public String reportQuestion() {
        return "report-question";
    }

    @GetMapping("/faq")
    public String faq() {
        return "FAQ";
    }

    @GetMapping("/driving-instructors")
    public String drivingInstructors() {
        return "driving-instructors";
    }

    @GetMapping("/oug-195-2002")
    public String oug1952002() {
        return "oug-195-2002";
    }

    @GetMapping("/traffic-regulations")
    public String trafficRegulations() {
        return "trafficRegulation";
    }

    @GetMapping("/terms")
    public String terms() {
        return "termsAndConditions";
    }

}
