package com.chestionare.chestionare360.Controller;

import com.chestionare.chestionare360.Model.Dto.FinishDuelRequest;
import com.chestionare.chestionare360.Model.Duel;
import com.chestionare.chestionare360.Model.DuelStatus;
import com.chestionare.chestionare360.Model.QuizQuestion;
import com.chestionare.chestionare360.Model.User;
import com.chestionare.chestionare360.Repository.DuelRepository;
import com.chestionare.chestionare360.Repository.QuizQuestionRepository;
import com.chestionare.chestionare360.Service.DuelService;
import com.chestionare.chestionare360.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/duel")
@RequiredArgsConstructor
public class DuelController {

    private final DuelService duelService;
    private final UserRepo userRepo;
    private final QuizQuestionRepository quizQuestionRepository;
    private final DuelRepository duelRepository;


    @GetMapping("/start")
    public String duelPage(Model model) {
        List<String> categories = List.of("A", "B", "C", "D", "E", "Tr", "13din15");
        model.addAttribute("categories", categories);
        return "duel-start";
    }

    @GetMapping("/game")
    public String duelGame(@RequestParam Long duelId,
                           @RequestParam String category,
                           Model model) {
        Duel duel = duelService.getDuel(duelId);
        List<QuizQuestion> questions = quizQuestionRepository.findRandomQuestionsByCategory(category, 10);
        model.addAttribute("duelId", duel.getId());
        model.addAttribute("duel", duel);
        model.addAttribute("questions", questions);
        return "duel-game";
    }


    @PostMapping("/find")
    @ResponseBody
    public Duel findDuel(@RequestParam int userId,
                         @RequestParam String category) {
        User user = userRepo.findById(userId).orElseThrow();
        return duelService.createSinglePlayerDuel(user, category);
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
    public void finishDuel(@RequestBody FinishDuelRequest request) {
        duelService.finishDuel(
                request.getDuelId(),
                request.getPlayer1Score(),
                request.getPlayer2Score()
        );
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

    @PostMapping("/create")
    @ResponseBody
    public Map<String, Object> createDuel(@RequestParam int userId, @RequestParam String category) {
        Optional<User> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User nu există");
        }

        Duel duel = new Duel();
        duel.setPlayer1(userOpt.get());
        duel.setCategory(category);
        duel.setStatus(DuelStatus.WAITING);
        duel.setCode(UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        duelRepository.save(duel);

        Map<String, Object> response = new HashMap<>();
        response.put("code", duel.getCode());
        response.put("duelId", duel.getId());
        return response;
    }


}
