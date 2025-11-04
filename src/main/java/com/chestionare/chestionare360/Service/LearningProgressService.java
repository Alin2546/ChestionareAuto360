package com.chestionare.chestionare360.Service;

import com.chestionare.chestionare360.Model.LearningProgress;
import com.chestionare.chestionare360.Model.User;
import com.chestionare.chestionare360.Repository.LearningProgressRepo;
import com.chestionare.chestionare360.Repository.QuizQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LearningProgressService {

    private final LearningProgressRepo learningProgressRepo;
    private final QuizQuestionRepository quizQuestionRepository;


    public LearningProgress createProgress(User user, String category) {

        Optional<LearningProgress> existing = learningProgressRepo.findByUser(user);
        if (existing.isPresent()) {
            return existing.get();
        }

        LearningProgress lp = new LearningProgress();
        lp.setUser(user);
        lp.setCategory(category);
        lp.setTimeRemaining(5);
        lp.setExpirationDate(LocalDateTime.now().plusDays(5));
        return learningProgressRepo.save(lp);
    }



    public int getProgressPercent(User user) {
        Optional<LearningProgress> optionalProgress = learningProgressRepo.findByUser(user);

        if (optionalProgress.isEmpty()) {
            return 0;
        }

        LearningProgress lp = optionalProgress.get();
        String category = lp.getCategory();
        long totalQuestions = quizQuestionRepository.countByCategory(category);

        if (totalQuestions == 0) return 0;

        return (int) ((double) lp.getCurrentQuestion() / totalQuestions * 100);
    }

    public Optional<LearningProgress> getProgress(User user) {
        return learningProgressRepo.findByUser(user);
    }






}
