package com.chestionare.chestionare360.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Duel {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User player1;

    @ManyToOne
    private User player2;

    private int player1Score;
    private int player2Score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DuelStatus status;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
}
