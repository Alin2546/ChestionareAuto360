package com.chestionare.chestionare360.Repository;

import com.chestionare.chestionare360.Model.ReportQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportQuestionRepository extends JpaRepository<ReportQuestion, Long> {
}
