package com.chestionare.chestionare360.Controller;

import com.chestionare.chestionare360.Model.QuizQuestion;
import com.chestionare.chestionare360.Repository.QuizQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SitemapController {

    private final QuizQuestionRepository quizQuestionRepository;

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public String sitemap() {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        String now = LocalDateTime.now().format(formatter);

        sb.append("<url>");
        sb.append("<loc>https://www.chestionareauto360.space/</loc>");
        sb.append("<lastmod>").append(now).append("</lastmod>");
        sb.append("<changefreq>daily</changefreq>");
        sb.append("<priority>1.0</priority>");
        sb.append("</url>");


        String[] staticPages = {
                "/donate",
                "/learning-environment",
                "/car-quizzes",
                "/drpciv-questions",
                "/report-question",
                "/faq",
                "/driving-instructors",
                "/oug-195-2002",
                "/traffic-regulations",
                "/terms"
        };

        for (String page : staticPages) {
            sb.append("<url>");
            sb.append("<loc>https://www.chestionareauto360.space").append(page).append("</loc>");
            sb.append("<lastmod>").append(now).append("</lastmod>");
            sb.append("<changefreq>weekly</changefreq>");
            sb.append("<priority>0.8</priority>");
            sb.append("</url>");
        }

        List<QuizQuestion> questions = quizQuestionRepository.findAll();
        for (QuizQuestion q : questions) {
            sb.append("<url>");
            sb.append("<loc>https://www.chestionareauto360.space/drpciv-questions/").append(q.getId()).append("</loc>");
            sb.append("<lastmod>").append(now).append("</lastmod>");
            sb.append("<changefreq>monthly</changefreq>");
            sb.append("<priority>0.6</priority>");
            sb.append("</url>");
        }

        sb.append("</urlset>");
        return sb.toString();
    }
}
