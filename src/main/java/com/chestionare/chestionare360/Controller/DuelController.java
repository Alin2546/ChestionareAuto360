package com.chestionare.chestionare360.Controller;

import com.chestionare.chestionare360.Model.Dto.FinishDuelRequest;
import com.chestionare.chestionare360.Model.Duel;
import com.chestionare.chestionare360.Model.DuelStatus;
import com.chestionare.chestionare360.Model.QuizQuestion;
import com.chestionare.chestionare360.Model.User;
import com.chestionare.chestionare360.Repository.DuelRepository;
import com.chestionare.chestionare360.Service.DuelService;
import com.chestionare.chestionare360.Repository.UserRepo;
import com.chestionare.chestionare360.Service.SecurityService.MyUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final DuelRepository duelRepository;


    @GetMapping("/start")
    public String duelPage(Model model, @AuthenticationPrincipal MyUser principal) {
        List<String> categories = List.of("A", "B", "C", "D", "E", "Tr", "13din15");
        model.addAttribute("categories", categories);
        if (principal != null) {
            User user = principal.getUser();
            model.addAttribute("userId", user.getId());
        } else {
            model.addAttribute("userId", null);
        }
        return "duel-start";
    }



    @GetMapping("/game")
    public String duelGame(@RequestParam Long duelId,
                           @RequestParam String category,
                           Model model) {
        Duel duel = duelService.getDuel(duelId);
        List<QuizQuestion> questions = duelService.getQuestionsForDuel(duel);
        model.addAttribute("duelId", duel.getId());
        model.addAttribute("duel", duel);
        model.addAttribute("questions", questions);
        return "duel-game";
    }

    @GetMapping("/{duelId}")
    public String duelView(@PathVariable Long duelId, Model model) {
        Duel duel = duelService.getDuel(duelId);
        model.addAttribute("duel", duel);
        return "duel-game";
    }

    @GetMapping("/status/{duelId}")
    @ResponseBody
    public Map<String, Object> getDuelStatus(@PathVariable Long duelId) {
        Duel duel = duelService.getDuel(duelId);
        Map<String, Object> response = new HashMap<>();
        response.put("status", duel.getStatus().name());
        return response;
    }


    @GetMapping("/json/{duelId}")
    @ResponseBody
    public Duel getDuelJson(@PathVariable Long duelId) {
        return duelService.getDuel(duelId);
    }

    @PostMapping("/computer")
    @ResponseBody
    public Duel computerDuel(
            @RequestParam(required = false) Integer userId,
            @RequestParam String category) {
        User user = null;
        if (userId != null) {
            user = userRepo.findById(userId).orElse(null);
        }
        return duelService.createSinglePlayerDuel(user, category);
    }


    @PostMapping("/join-by-code")
    @ResponseBody
    public Map<String, Object> joinFriendDuel(
            @RequestParam String code,
            @RequestParam(required = false) Integer userId) {
        Duel duel = duelRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Duel nu există"));
        User player2 = null;
        if (userId != null) {
            player2 = userRepo.findById(userId).orElse(null);
        }
        duel = duelService.joinFriendDuel(duel.getId(), player2);
        duel.setStatus(DuelStatus.IN_PROGRESS);
        duelRepository.save(duel);

        Map<String, Object> response = new HashMap<>();
        response.put("duelId", duel.getId());
        response.put("success", true);
        response.put("category", duel.getCategory());
        return response;
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

    @PostMapping("/create-friend")
    @ResponseBody
    public Map<String, Object> createFriendDuel(
            @RequestParam(required = false) Integer userId,
            @RequestParam String category) {

        User player1 = null;
        if (userId != null) {
            player1 = userRepo.findById(userId).orElse(null);
        }

        Duel duel = duelService.createFriendDuel(player1, category);

        Map<String, Object> response = new HashMap<>();
        response.put("duelId", duel.getId());
        response.put("code", duel.getCode());
        response.put("category", duel.getCategory());
        return response;
    }



}