package com.chestionare.chestionare360.Controller;

import com.chestionare.chestionare360.Model.QuizQuestion;
import com.chestionare.chestionare360.Repository.QuizQuestionRepository;
import com.chestionare.chestionare360.Service.QuizResultService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuizController {

    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizResultService quizResultService;


    @GetMapping("/{category}")
    public String showQuiz(@PathVariable String category, Model model) {
        List<QuizQuestion> questions = quizQuestionRepository.findRandomByCategory(category);
        model.addAttribute("questions", questions);
        model.addAttribute("quizName", category);
        return "quiz";
    }


    @PostMapping("/submit")
    public String submitQuiz(@RequestParam Map<String, String> answers,
                             @RequestParam String category,
                             Principal principal,
                             Model model) {

        int finalScore = quizResultService.calculateScore(answers);

        if (principal != null) {
            quizResultService.saveResult(principal.getName(), category, finalScore);
        }

        String message;
        if (finalScore >= 20) {
            message = "Felicitări! Ai fost admis!";
        } else {
            message = "Îmi pare rău, nu ai promovat testul. Mai încearcă!";
        }

        model.addAttribute("score", finalScore);
        model.addAttribute("quizMessage", message);

        List<QuizQuestion> questions = quizQuestionRepository.findRandomByCategory(category);
        model.addAttribute("questions", questions);
        model.addAttribute("quizName", category);

        return "quiz";
    }

    @GetMapping("/learning/{category}")
    public String showAllLearningQuestions(@PathVariable String category, Model model) {
        List<QuizQuestion> questions = quizQuestionRepository.findByCategory(category);
        model.addAttribute("questions", questions);
        model.addAttribute("quizName", category);
        return "learning-environment-quiz";
    }
}
