package com.chestionare.chestionare360.Controller;

import com.chestionare.chestionare360.Model.Duel;
import com.chestionare.chestionare360.Model.QuizQuestion;
import com.chestionare.chestionare360.Model.User;
import com.chestionare.chestionare360.Repository.QuizQuestionRepository;
import com.chestionare.chestionare360.Service.DuelService;
import com.chestionare.chestionare360.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/duel")
@RequiredArgsConstructor
public class DuelController {

    private final DuelService duelService;
    private final UserRepo userRepo;
    private final QuizQuestionRepository quizQuestionRepository;


    @GetMapping("/start")
    public String duelPage() {
        return "duel-start";
    }

    @GetMapping("/game")
    public String duelGame(Model model) {
        List<QuizQuestion> questions = quizQuestionRepository.findRandomQuestions(10);
        model.addAttribute("questions", questions);
        return "duel-game";
    }

    @PostMapping("/find")
    @ResponseBody
    public Duel findDuel(@RequestParam int userId) {
        User user = userRepo.findById(userId).orElseThrow();
        return duelService.findOrCreateDuel(user);
    }

    @PostMapping("/join")
    @ResponseBody
    public Duel joinDuel(
            @RequestParam Long duelId,
            @RequestParam int userId
    ) {
        User user = userRepo.findById(userId).orElseThrow();
        return duelService.joinDuel(duelId, user);
    }

    @PostMapping("/answer")
    @ResponseBody
    public void submitAnswer(
            @RequestParam Long duelId,
            @RequestParam int userId,
            @RequestParam boolean correct,
            @RequestParam long timeSpent
    ) {
        User user = userRepo.findById(userId).orElseThrow();
        duelService.submitAnswer(duelId, user, correct, timeSpent);
    }

    @PostMapping("/finish")
    @ResponseBody
    public void finishDuel(@RequestParam Long duelId) {
        duelService.finishDuel(duelId);
    }

    @GetMapping("/{duelId}")
    public String duelView(@PathVariable Long duelId, Model model) {
        Duel duel = duelService.getDuel(duelId);
        model.addAttribute("duel", duel);
        return "duel-game";
    }

    @GetMapping("/get-random-questions")
    @ResponseBody
    public List<QuizQuestion> getRandomQuestions(@RequestParam(defaultValue = "10") int limit) {
        return quizQuestionRepository.findRandomQuestions(limit);
    }

}
