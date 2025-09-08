package com.chestionare.chestionare360.Controller;

import com.chestionare.chestionare360.Model.Dto.UserCreateDto;

import com.chestionare.chestionare360.Model.QuizResult;

import com.chestionare.chestionare360.Service.QuizResultService;
import com.chestionare.chestionare360.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;



@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final QuizResultService quizResultService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("userCreateDto", new UserCreateDto());
        return "login";
    }

    @PostMapping("/login")
    public String loginOrRegister(@ModelAttribute @Valid UserCreateDto userCreateDto,
                                  BindingResult bindingResult,
                                  HttpServletRequest request,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userCreateDto", userCreateDto);
            model.addAttribute("error", "Parola pentru numarul de telefon " + userCreateDto.getPhoneNumber() + " invalida");
            return "login";
        }
        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userCreateDto.getPhoneNumber(), userCreateDto.getPassword());
            Authentication authentication = authenticationManager.authenticate(authToken);

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);

            HttpSession session = request.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    context
            );

            return "redirect:/";
        } catch (AuthenticationException e) {
            model.addAttribute("error", "Număr de telefon sau parola incorecte");
            return "login";
        }
    }

    @GetMapping("/account")
    public String getYourAccount(Model model, Principal principal){
        String username = principal.getName();

        List<QuizResult> quizResults = quizResultService.getResultsForUser(principal.getName());

        model.addAttribute("quizResults", quizResults);
        model.addAttribute("username", principal.getName());

        return "account";
    }
}
