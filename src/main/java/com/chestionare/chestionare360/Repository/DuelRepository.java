package com.chestionare.chestionare360.Repository;

import com.chestionare.chestionare360.Model.Duel;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface DuelRepository extends JpaRepository<Duel, Long> {

    Optional<Duel> findByCode(String code);
}
