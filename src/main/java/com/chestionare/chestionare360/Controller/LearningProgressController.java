package com.chestionare.chestionare360.Controller;

import com.chestionare.chestionare360.Model.LearningProgress;
import com.chestionare.chestionare360.Model.User;
import com.chestionare.chestionare360.Repository.LearningProgressRepo;
import com.chestionare.chestionare360.Repository.UserRepo;
import com.chestionare.chestionare360.Service.LearningProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class LearningProgressController {

    private final LearningProgressService learningProgressService;
    private final LearningProgressRepo learningProgressRepo;
    private final UserRepo userRepo;

    @PostMapping("/save-progress")
    @ResponseBody
    public ResponseEntity<?> saveProgress(@RequestParam int currentQuestionIndex,
                                          java.security.Principal principal) {
        String phone = principal.getName();
        User user = userRepo.findByPhoneNumber(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LearningProgress progress = learningProgressService.getProgress(user)
                .orElseThrow(() -> new RuntimeException("Progress not found"));
        progress.setCurrentQuestion(currentQuestionIndex);
        learningProgressRepo.save(progress);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-progress")
    @ResponseBody
    public ResponseEntity<Integer> getProgress(java.security.Principal principal) {
        String phone = principal.getName();
        User user = userRepo.findByPhoneNumber(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LearningProgress progress = learningProgressService.getProgress(user)
                .orElse(null);

        if (progress == null) {
            return ResponseEntity.ok(0);
        }
        return ResponseEntity.ok(progress.getCurrentQuestion());
    }

    @PostMapping("/reset-progress")
    public String resetProgress(java.security.Principal principal) {
        String phone = principal.getName();
        User user = userRepo.findByPhoneNumber(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LearningProgress progress = learningProgressService.getProgress(user)
                .orElseThrow(() -> new RuntimeException("Progress not found"));

        user.setHasCompletedLearningEnvironment(false);
        learningProgressRepo.delete(progress);

        return "redirect:/account";
    }
    @PostMapping("/complete-learning")
    @ResponseBody
    public ResponseEntity<?> completeLearning(java.security.Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String phone = principal.getName();
        User user = userRepo.findByPhoneNumber(phone)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setHasCompletedLearningEnvironment(true);
        userRepo.save(user);

        return ResponseEntity.ok("Learning environment marked as complete");
    }

}
