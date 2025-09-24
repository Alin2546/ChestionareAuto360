package com.chestionare.chestionare360.Controller;

import com.chestionare.chestionare360.Model.ReportQuestion;
import com.chestionare.chestionare360.Repository.ReportQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ReportQuestionController {

    private final ReportQuestionRepository reportQuestionRepository;

    @PostMapping("/report-question")
    public String submitReport(ReportQuestion reportQuestion) {
        reportQuestionRepository.save(reportQuestion);
        return "redirect:/report-success";
    }

    @GetMapping("/report-success")
    public String reportSuccess() {
        return "report-success";
    }
}
