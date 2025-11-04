package com.chestionare.chestionare360.Controller;


import com.chestionare.chestionare360.Model.LearningProgress;
import com.chestionare.chestionare360.Model.QuizQuestion;
import com.chestionare.chestionare360.Model.User;
import com.chestionare.chestionare360.Repository.LearningProgressRepo;
import com.chestionare.chestionare360.Repository.QuizQuestionRepository;
import com.chestionare.chestionare360.Repository.UserRepo;
import com.chestionare.chestionare360.Service.LearningProgressService;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final QuizQuestionRepository quizQuestionRepository;
    private final UserRepo userRepo;
    private final LearningProgressService learningProgressService;
    private final LearningProgressRepo learningProgressRepo;

    @GetMapping
    public String homePage() {
        return "home";
    }

    @GetMapping("/donate")
    public String showDonatePage() {
        return "philanthropy";
    }

    @GetMapping("/learning-environment")
    public String learningEnvironment(@RequestParam(required = false, defaultValue = "B") String category,
                                      Model model,
                                      java.security.Principal principal) {
        List<String> categories = List.of("A", "B", "C", "D", "E", "Tr", "13din15");

        model.addAttribute("categories", categories);
        model.addAttribute("category", category);

        if (principal != null) {
            String phone = principal.getName();
            User user = userRepo.findByPhoneNumber(phone)
                    .orElseThrow(() -> new RuntimeException("User not found"));


            Optional<LearningProgress> progressOpt = learningProgressService.getProgress(user);

            if (progressOpt.isPresent()) {
                LearningProgress progress = progressOpt.get();
                long questions = quizQuestionRepository.countByCategory(progress.getCategory());

                if (progress.getCurrentQuestion() >= questions) {
                    return "learning-complete";
                }
                return "redirect:quiz/learning/" + progress.getCategory();
            }
        }
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
    public String drpcivQuestions(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<QuizQuestion> questionPage = quizQuestionRepository.findAll(pageable);
        model.addAttribute("questions", questionPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", questionPage.getTotalPages());
        model.addAttribute("pageSize", size);

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
