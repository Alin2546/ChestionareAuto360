package com.chestionare.chestionare360.Model.Dto;


import com.chestionare.chestionare360.Model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto {

    @NotBlank(message = "Numărul de telefon este obligatoriu")
    @Pattern(
            regexp = "^(\\+?40|0)?7[0-9]{8}$",
            message = "Număr de telefon invalid. Trebuie să înceapă cu 07, 407 sau +407 și să aibă 10 cifre"
    )
    private String phoneNumber;


    @NotBlank(message = "Parola este obligatorie")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,50}$", message = "Parola trebuie sa contina cel putin o majuscula si o cifra")
    private String password;

    private String role = "ROLE_USER";


    public User mapToUser() {
        User user = new User();
        user.setPhoneNumber(this.phoneNumber);
        user.setPassword(this.password);
        user.setRole(this.role);
        return user;
    }
}
