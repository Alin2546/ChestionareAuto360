package com.chestionare.chestionare360.Controller;

import com.chestionare.chestionare360.Model.Dto.UserCreateDto;
import com.chestionare.chestionare360.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("userCreateDto", new UserCreateDto());
        return "login"; // numele view-ului Thymeleaf (login.html)
    }

//    @PostMapping("/login")
//    public String loginOrRegister(@Valid @ModelAttribute("userCreateDto") UserCreateDto userCreateDto,
//                                  BindingResult bindingResult,
//                                  Model model) {
//        if (bindingResult.hasErrors()) {
//            return "login";
//        }
//
//        try {
//            String message = userService.loginOrRegister(userCreateDto);
//            model.addAttribute("message", message);
//            return "redirect:/learning-environment"; // sau pagina dorită după login
//        } catch (ResponseStatusException ex) {
//            model.addAttribute("error", ex.getReason());
//            return "login";
//        }
//    }
}
