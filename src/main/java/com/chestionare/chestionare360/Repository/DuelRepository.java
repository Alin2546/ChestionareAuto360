package com.chestionare.chestionare360.Repository;

import com.chestionare.chestionare360.Model.Duel;
import com.chestionare.chestionare360.Model.DuelStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DuelRepository extends JpaRepository<Duel, Long> {

    List<Duel> findByStatus(DuelStatus status);

    boolean existsByPlayer1IdOrPlayer2IdAndStatus(
            int player1Id,
            int player2Id,
            DuelStatus status
    );
}
