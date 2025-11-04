package com.chestionare.chestionare360.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "quiz_questions")
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    @Column(length = 2000)
    private String text;

    @Column(length = 1000)
    private String optionA;

    @Column(length = 1000)
    private String optionB;

    @Column(length = 1000)
    private String optionC;

    private String correctOption;

    private String imageUrl;
}
