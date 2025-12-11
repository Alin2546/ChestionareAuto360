package com.chestionare.chestionare360.Controller;

import com.chestionare.chestionare360.Model.Dto.UserCreateDto;

import com.chestionare.chestionare360.Model.LearningProgress;
import com.chestionare.chestionare360.Model.QuizResult;

import com.chestionare.chestionare360.Model.User;
import com.chestionare.chestionare360.Repository.QuizResultRepository;
import com.chestionare.chestionare360.Repository.UserRepo;
import com.chestionare.chestionare360.Service.LearningProgressService;
import com.chestionare.chestionare360.Service.QuizResultService;
import com.chestionare.chestionare360.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
    private final QuizResultService quizResultService;
    private final PasswordEncoder passwordEncoder;
    private final LearningProgressService learningProgressService;
    private final QuizResultRepository quizResultRepository;


    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("userCreateDto", new UserCreateDto());
        return "login";
    }

    @PostMapping("/login")
    public String loginOrRegister(@ModelAttribute @Valid UserCreateDto userCreateDto,
                                  BindingResult bindingResult,
                                  HttpServletRequest request,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userCreateDto", userCreateDto);
            model.addAttribute("error", "Parola pentru numarul de telefon " + userCreateDto.getPhoneNumber() + " invalida");
            return "login";
        }

        Optional<User> optionalUser = userRepo.findByPhoneNumber(userCreateDto.getPhoneNumber());

        User user;
        if (optionalUser.isEmpty()) {
            user = new User();
            user.setPhoneNumber(userCreateDto.getPhoneNumber());
            user.setRole("USER_ROLE");
            user.setActive(true);
            user.setPassword(passwordEncoder.encode(userCreateDto.getPassword()));
            userRepo.save(user);
        } else {
            user = optionalUser.get();
        }

        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userCreateDto.getPhoneNumber(), userCreateDto.getPassword());
            Authentication authentication = authenticationManager.authenticate(authToken);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

            return "redirect:/";
        } catch (AuthenticationException e) {
            model.addAttribute("error", "Număr de telefon sau parola incorecte");
            return "login";
        }
    }

    @GetMapping("/account")
    public String getYourAccount(@RequestParam(defaultValue = "B") String category,
                                 @RequestParam(defaultValue = "0") int page,
                                 Model model, Principal principal) {

        String username = principal.getName();
        User user = userRepo.findByPhoneNumber(username)
                .orElseThrow(() -> new RuntimeException("User not found"));


        int pageSize = 12;
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(Sort.Direction.DESC, "completedAt"));
        Page<QuizResult> quizPage = quizResultRepository.findByUser(user, pageRequest);

        int progressPercent = learningProgressService.getProgressPercent(user);
        Optional<LearningProgress> progressOpt = learningProgressService.getProgress(user);

        model.addAttribute("userId", user.getId());
        model.addAttribute("username", username);
        model.addAttribute("category", category);
        model.addAttribute("user", user);
        model.addAttribute("learning", progressOpt.orElse(null));
        model.addAttribute("progressPercent", progressPercent);
        model.addAttribute("quizPage", quizPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", quizPage.getTotalPages());

        return "account";
    }


    @GetMapping("/statistics")
    public String getStatistics(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepo.findByPhoneNumber(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isHasCompletedLearningEnvironment()) {
            return "redirect:/account";
        }

        Optional<LearningProgress> progressOpt = learningProgressService.getProgress(user);

        if (progressOpt.isEmpty() || learningProgressService.getProgressPercent(user) < 100) {
            return "redirect:/account";
        }

        LearningProgress progress = progressOpt.get();
        String completedCategory = progress.getCategory();
        int progressPercent = learningProgressService.getProgressPercent(user);

        List<QuizResult> allResults = quizResultService.getResultsForUser(username);
        List<QuizResult> quizResults = allResults.stream()
                .filter(q -> q.getCategory().equalsIgnoreCase(completedCategory))
                .sorted((a, b) -> b.getCompletedAt().compareTo(a.getCompletedAt()))
                .toList();

        model.addAttribute("quizResults", quizResults);
        model.addAttribute("username", username);

        int totalQuizzes = quizResults.size();
        long passedQuizzes = quizResults.stream().filter(q -> q.getScore() >= 22).count();
        long failedQuizzes = totalQuizzes - passedQuizzes;

        model.addAttribute("totalQuizzes", totalQuizzes);
        model.addAttribute("passedQuizzes", passedQuizzes);
        model.addAttribute("failedQuizzes", failedQuizzes);

        List<User> allUsers = userRepo.findAll();
        List<Map<String, Object>> leaderboard = allUsers.stream()
                .filter(User::isHasCompletedLearningEnvironment)
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("user", u);
                    map.put("progressPercent", u.getProgressByCategory().getOrDefault(completedCategory, 0.0));
                    map.put("isCurrentUser", u.getPhoneNumber().equals(username));
                    return map;
                })
                .filter(entry -> (double) entry.get("progressPercent") > 10)
                .sorted((a, b) -> Double.compare((double) b.get("progressPercent"), (double) a.get("progressPercent")))
                .toList();

        int currentUserRank = -1;
        for (int i = 0; i < leaderboard.size(); i++) {
            Map<String, Object> entry = leaderboard.get(i);
            User u = (User) entry.get("user");
            if (u.getPhoneNumber().equals(username)) {
                currentUserRank = i + 1;
                break;
            }
        }

        double currentUserProgress = user.getProgressByCategory().getOrDefault(completedCategory, 0.0);

        model.addAttribute("progressPercent", currentUserProgress);
        model.addAttribute("category", completedCategory);
        model.addAttribute("currentUserRank", currentUserRank == -1 ? "N/A" : currentUserRank);
        model.addAttribute("leaderboard", leaderboard);

        return "statistics";
    }


    @GetMapping("/statistics/user/{phone}")
    @ResponseBody
    public Map<String, Object> getUserStats(@PathVariable String phone) {
        User user = userRepo.findByPhoneNumber(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<QuizResult> results = quizResultService.getResultsForUser(phone);

        long passed = results.stream().filter(r -> r.getScore() >= 22).count();
        long failed = results.size() - passed;

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", results.size());
        stats.put("passed", passed);
        stats.put("failed", failed);
        return stats;
    }

    @PostMapping("/update-profile-image")
    @ResponseBody
    public String updateProfileImage(@RequestParam("imageUrl") String imageUrl,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepo.findByPhoneNumber(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        if (user == null) {
            return "Utilizator inexistent";
        }

        user.setProfileImageUrl(imageUrl);
        userRepo.save(user);
        return "Imagine actualizată cu succes";
    }

    @GetMapping("/forgotPassword")
    public String showForgotPasswordForm() {
        return "forgotPassword";
    }
}

