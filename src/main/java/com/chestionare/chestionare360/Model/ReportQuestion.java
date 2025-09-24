package com.chestionare.chestionare360.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ReportQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    private String answerA;
    private String answerB;
    private String answerC;

    @Column(columnDefinition = "TEXT")
    private String imageDesc;

    private String email;
}
