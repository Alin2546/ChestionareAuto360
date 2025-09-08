package com.chestionare.chestionare360.Repository;

import com.chestionare.chestionare360.Model.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByCategory(String category);

    boolean existsByCategory(String category);

    @Query("SELECT DISTINCT q.category FROM QuizQuestion q")
    List<String> findDistinctCategories();
}

