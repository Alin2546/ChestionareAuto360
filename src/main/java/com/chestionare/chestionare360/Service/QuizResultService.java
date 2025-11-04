package com.chestionare.chestionare360.Service;

import com.chestionare.chestionare360.Model.QuizQuestion;
import com.chestionare.chestionare360.Model.QuizResult;
import com.chestionare.chestionare360.Model.User;
import com.chestionare.chestionare360.Repository.QuizQuestionRepository;
import com.chestionare.chestionare360.Repository.QuizResultRepository;
import com.chestionare.chestionare360.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuizResultService {

    private final QuizResultRepository quizResultRepository;
    private final UserService userService;
    private final QuizQuestionRepository quizQuestionRepository;
    private final UserRepo userRepo;

    public void saveResult(String email, String category, int score) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with: " + email + " not found"));

        QuizResult result = new QuizResult();
        result.setUser(user);
        result.setCategory(category);
        result.setScore(score);
        result.setCompletedAt(LocalDateTime.now());

        quizResultRepository.save(result);
        updateUserProgress(user, category);
    }

    private void updateUserProgress(User user, String category) {
        int total = quizResultRepository.countByUserAndCategory(user, category);
        int passed = quizResultRepository.countByUserAndCategoryAndScoreGreaterThanEqual(user, category, 22);
        int failed = total - passed;

        double gauge = calculateGauge(total, passed, failed);
        user.getProgressByCategory().put(category, gauge);
        userRepo.save(user);
    }


    public double calculateGauge(int total, int passed, int failed) {
        if (total < 15) return 10;

        double base, weightPassed, weightFailed, maxGauge;

        if (total >= 290) { base = 96; weightPassed = 1.0; weightFailed = 0.05; maxGauge = 99; }
        else if (total >= 220) { base = 80; weightPassed = 0.8; weightFailed = 0.05; maxGauge = 90; }
        else if (total >= 180) { base = 70; weightPassed = 0.8; weightFailed = 0.1; maxGauge = 85; }
        else if (total >= 100) { base = 60; weightPassed = 0.7; weightFailed = 0.2; maxGauge = 80; }
        else if (total >= 50) { base = 45; weightPassed = 0.6; weightFailed = 0.2; maxGauge = 65; }
        else { base = 10; weightPassed = 1.0; weightFailed = 0.1; maxGauge = 40; }

        double effectiveProgress = (passed * weightPassed + failed * weightFailed) / total * 100;
        double gauge = base + (maxGauge - base) * (effectiveProgress / 100);

        return Math.min(gauge, maxGauge);
    }

    public List<QuizResult> getResultsForUser(String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with: " + email + " not found"));
        return quizResultRepository.findByUserId(user.getId());
    }

    public int calculateScore(Map<String, String> answers) {
        int score = 0;

        for (Map.Entry<String, String> entry : answers.entrySet()) {
            String key = entry.getKey();
            if (!key.matches("\\d+")) {
                continue;
            }

            Long questionId = Long.parseLong(key);
            String chosenOption = entry.getValue();

            QuizQuestion question = quizQuestionRepository.findById(questionId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid question id: " + questionId));

            if (question.getCorrectOption().equalsIgnoreCase(chosenOption)) {
                score++;
            }
        }

        return score;
    }


}


