package com.chestionare.chestionare360.Repository;

import com.chestionare.chestionare360.Model.QuizResult;
import com.chestionare.chestionare360.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByUserId(int userId);

    Page<QuizResult> findByUser(User user, Pageable pageable);

    int countByUserAndCategory(User user, String category);

    int countByUserAndCategoryAndScoreGreaterThanEqual(User user, String category, int i);
}
