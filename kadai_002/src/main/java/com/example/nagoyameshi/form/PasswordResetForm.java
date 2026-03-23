package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetForm {

    @NotBlank
    @Size(min = 8, message = "パスワードは8文字以上で入力してください。")
    private String password;

    @NotBlank
    private String passwordConfirmation;
}
