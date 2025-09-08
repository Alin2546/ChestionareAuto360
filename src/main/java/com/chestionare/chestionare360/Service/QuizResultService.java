package com.chestionare.chestionare360.Service;

import com.chestionare.chestionare360.Model.QuizQuestion;
import com.chestionare.chestionare360.Model.QuizResult;
import com.chestionare.chestionare360.Model.User;
import com.chestionare.chestionare360.Repository.QuizQuestionRepository;
import com.chestionare.chestionare360.Repository.QuizResultRepository;
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

    public QuizResult saveResult(String email, String category, int score) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with: " + email + " not found"));

        QuizResult result = new QuizResult();
        result.setUser(user);
        result.setCategory(category);
        result.setScore(score);
        result.setCompletedAt(LocalDateTime.now());

        return quizResultRepository.save(result);
    }

    public List<QuizResult> getResultsForUser(String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with: " + email + " not found"));
        return quizResultRepository.findByUserId(user.getId());
    }

    public int calculateScore(Map<String, String> answers) {
        int score = 0;

        for (Map.Entry<String, String> entry : answers.entrySet()) {
            Long questionId = Long.parseLong(entry.getKey());
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


