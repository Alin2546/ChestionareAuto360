package com.chestionare.chestionare360.Repository;

import com.chestionare.chestionare360.Model.LearningProgress;
import com.chestionare.chestionare360.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LearningProgressRepo extends JpaRepository<LearningProgress, Long> {
    Optional<LearningProgress> findByUser(User user);



}
