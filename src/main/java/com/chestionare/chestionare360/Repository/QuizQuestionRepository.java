package com.chestionare.chestionare360.Repository;

import com.chestionare.chestionare360.Model.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    List<QuizQuestion> findByCategory(String category);

    boolean existsByCategory(String category);

    @Query("SELECT COUNT(q) FROM QuizQuestion q WHERE q.category = :category")
    long countByCategory(@Param("category") String category);

    @Query(value = "SELECT * FROM quiz_questions WHERE category = :category ORDER BY RANDOM() LIMIT 26", nativeQuery = true)
    List<QuizQuestion> findRandomByCategory(@Param("category") String category);

    @Query(value = "SELECT * FROM quiz_questions ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<QuizQuestion> findRandomQuestions(@Param("limit") int limit);


}

