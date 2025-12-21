package com.chestionare.chestionare360.Controller;

import com.chestionare.chestionare360.Model.LearningProgress;
import com.chestionare.chestionare360.Model.QuizQuestion;
import com.chestionare.chestionare360.Model.User;
import com.chestionare.chestionare360.Repository.QuizQuestionRepository;
import com.chestionare.chestionare360.Repository.UserRepo;
import com.chestionare.chestionare360.Service.LearningProgressService;
import com.chestionare.chestionare360.Service.QuizResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizResultService quizResultService;
    private final LearningProgressService learningProgressService;
    private final UserRepo userRepo;

    @GetMapping("/{category}")
    public String showQuiz(@PathVariable String category, Model model) {
        List<QuizQuestion> questions = quizQuestionRepository.findRandomByCategory(category);
        model.addAttribute("questions", questions);
        model.addAttribute("quizName", category);
        return "quiz";
    }

    @PostMapping("/submit")
    @ResponseBody
    public Map<String, Object> submitQuiz(@RequestBody Map<String, Object> payload,
                                          Principal principal) {

        String category = (String) payload.get("category");
        Map<String, String> answers = (Map<String, String>) payload.get("answers");

        int finalScore = quizResultService.calculateScore(answers);

        if (principal != null) {
            quizResultService.saveResult(principal.getName(), category, finalScore);
        }

        String message = finalScore >= 22
                ? "Felicitări! Ai fost admis!"
                : "Îmi pare rău, nu ai promovat testul. Mai încearcă!";

        Map<String, Object> response = new HashMap<>();
        response.put("score", finalScore);
        response.put("message", message);
        return response;
    }

    @GetMapping("/learning/{category}")
    public String showAllLearningQuestions(@PathVariable String category,
                                           Model model,
                                           java.security.Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String phone = principal.getName();
        User user = userRepo.findByPhoneNumber(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));
        LearningProgress progress = learningProgressService.getProgress(user)
                .orElseGet(() -> learningProgressService.createProgress(user, category));
        List<QuizQuestion> questions = quizQuestionRepository.findByCategory(category);
        model.addAttribute("questions", questions);
        model.addAttribute("category", category);
        return "learning-environment-quiz";
    }
}
