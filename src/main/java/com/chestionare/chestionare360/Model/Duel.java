package com.chestionare.chestionare360.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Duel {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = true)
    @JoinColumn(name = "player1_id", nullable = true)
    private User player1;

    @ManyToOne(optional = true)
    @JoinColumn(name = "player2_id", nullable = true)
    private User player2;

    private String DuelType;

    private int player1Score;
    private int player2Score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DuelStatus status;

    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    private String category;

    @Column(unique = true)
    private String code;

}
