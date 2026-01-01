package com.chestionare.chestionare360.Service;

import com.chestionare.chestionare360.Model.Duel;
import com.chestionare.chestionare360.Model.DuelStatus;
import com.chestionare.chestionare360.Model.QuizQuestion;
import com.chestionare.chestionare360.Model.User;
import com.chestionare.chestionare360.Repository.DuelRepository;
import com.chestionare.chestionare360.Repository.QuizQuestionRepository;
import com.chestionare.chestionare360.Repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
@RequiredArgsConstructor
public class DuelService {

    private final DuelRepository duelRepository;
    private final UserRepo userRepository;
    private final QuizQuestionRepository quizQuestionRepository;

    private final ConcurrentHashMap<Long, List<QuizQuestion>> duelQuestions = new ConcurrentHashMap<>();

    public List<QuizQuestion> getQuestionsForDuel(Duel duel) {
        return duelQuestions.computeIfAbsent(duel.getId(), id ->
                quizQuestionRepository.findRandomQuestionsByCategory(duel.getCategory(), 10)
        );
    }

    public void submitAnswer(Long duelId, User user, boolean correct, long timeSpentMillis) {
        Duel duel = getDuel(duelId);
        if (duel.getStatus() != DuelStatus.IN_PROGRESS) {
            throw new IllegalStateException("Duel not active");
        }
        int points = calculatePoints(timeSpentMillis, correct);
        if (user.getId() == duel.getPlayer1().getId()) {
            duel.setPlayer1Score(duel.getPlayer1Score() + points);
        } else {
            duel.setPlayer2Score(duel.getPlayer2Score() + points);
        }
        duelRepository.save(duel);
    }

    public Duel createDuel(User player1, User player2, String category, String duelType, DuelStatus status) {
        Duel duel = new Duel();
        duel.setPlayer1(player1);
        duel.setPlayer2(player2);
        duel.setCategory(category);
        duel.setDuelType(duelType);
        duel.setStatus(status);
        duel.setCode(UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        duel.setStartedAt(status == DuelStatus.IN_PROGRESS ? LocalDateTime.now() : null);
        duel.setPlayer1Score(0);
        duel.setPlayer2Score(0);
        return duelRepository.save(duel);
    }

    public Duel createSinglePlayerDuel(User player1, String category) {
        return createDuel(player1, null, category, "Computer Duel", DuelStatus.IN_PROGRESS);
    }

    public Duel createFriendDuel(User player1, String category) {
        return createDuel(player1, null, category, "Friend Duel", DuelStatus.WAITING);
    }

    public Duel joinFriendDuel(Long duelId, User player2) {
        Duel duel = getDuel(duelId);
        if (duel.getStatus() != DuelStatus.WAITING) {
            throw new IllegalStateException("Duelul nu poate fi accesat");
        }
        duel.setPlayer2(player2);
        duel.setStatus(DuelStatus.IN_PROGRESS);
        duel.setStartedAt(LocalDateTime.now());
        return duelRepository.save(duel);
    }


    public void finishDuel(Long duelId, int p1Score, int p2Score) {
        Duel duel = duelRepository.findById(duelId)
                .orElseThrow(() -> new RuntimeException("Duel not found"));

        duel.setPlayer1Score(p1Score);
        duel.setPlayer2Score(p2Score);
        duel.setStatus(DuelStatus.FINISHED);

        if (duel.getPlayer1() != null || duel.getPlayer2() != null) {
            updateStats(duel);
        }

        duelRepository.save(duel);
    }

    public Duel getDuel(Long duelId) {
        return duelRepository.findById(duelId)
                .orElseThrow(() -> new IllegalArgumentException("Duel not found"));
    }

    private int calculatePoints(long timeSpentMillis, boolean correct) {
        if (!correct) return 0;
        if (timeSpentMillis < 3000) return 10;
        if (timeSpentMillis < 7000) return 7;
        return 5;
    }

    private void updateStats(Duel duel) {
        User p1 = duel.getPlayer1();
        User p2 = duel.getPlayer2();

        if (p1 != null) {
            p1.setDuelsPlayed(p1.getDuelsPlayed() + 1);
        }

        if (p2 != null) {
            p2.setDuelsPlayed(p2.getDuelsPlayed() + 1);
        }

        if (p1 != null && p2 != null) {
            if (duel.getPlayer1Score() > duel.getPlayer2Score()) {
                p1.setDuelsWon(p1.getDuelsWon() + 1);
                p2.setDuelsLost(p2.getDuelsLost() + 1);
            } else if (duel.getPlayer1Score() < duel.getPlayer2Score()) {
                p2.setDuelsWon(p2.getDuelsWon() + 1);
                p1.setDuelsLost(p1.getDuelsLost() + 1);
            } else {
                p1.setDuelsDraw(p1.getDuelsDraw() + 1);
                p2.setDuelsDraw(p2.getDuelsDraw() + 1);
            }
        }

        if (p1 != null) userRepository.save(p1);
        if (p2 != null) userRepository.save(p2);
    }


}