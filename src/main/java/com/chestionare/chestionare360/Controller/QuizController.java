package com.chestionare.chestionare360.Controller;

import com.chestionare.chestionare360.Model.QuizQuestion;
import com.chestionare.chestionare360.Repository.QuizQuestionRepository;
import com.chestionare.chestionare360.Service.QuizResultService;
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
        List<QuizQuestion> questions = quizQuestionRepository.findByCategory(category);
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

        quizResultService.saveResult(principal.getName(), category, finalScore);

        model.addAttribute("score", finalScore);
        return "quizResult";
    }

}
