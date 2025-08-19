package com.chestionare.chestionare360.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping
    public String homePage(){
        return "home";
    }

    @GetMapping("/donate")
    public String showDonatePage() {
        return "philanthropy";
    }

    @GetMapping("/learning-environment")
    public String learningEnvironment() {
        return "learning-environment";
    }

    @GetMapping("/car-quizzes")
    public String carQuizzes() {
        return "car-quizzes";
    }

    @GetMapping("/drpciv-questions")
    public String drpcivQuestions() {
        return "drpciv-questions";
    }

    @GetMapping("/report-question")
    public String reportQuestion() {
        return "report-question";
    }

    @GetMapping("/faq")
    public String faq() {
        return "FAQ";
    }

    @GetMapping("/driving-instructors")
    public String drivingInstructors() {
        return "driving-instructors";
    }

    @GetMapping("/oug-195-2002")
    public String oug1952002() {
        return "oug-195-2002";
    }

    @GetMapping("/traffic-regulations")
    public String trafficRegulations() {
        return "trafficRegulation";
    }

    @GetMapping("/terms")
    public String terms() {
        return "termsAndConditions";
    }

}
