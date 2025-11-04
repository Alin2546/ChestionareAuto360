package com.chestionare.chestionare360.Model;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
    @Entity
    @Table(name = "users")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;

        private String phoneNumber;
        private String password;
        private String role;
        private boolean isActive;
        private boolean hasCompletedLearningEnvironment;

        @ElementCollection
        @CollectionTable(name = "user_category_progress", joinColumns = @JoinColumn(name = "user_id"))
        @MapKeyColumn(name = "category")
        @Column(name = "progress")
        private Map<String, Double> progressByCategory = new HashMap<>();

        private String profileImageUrl;
    }







