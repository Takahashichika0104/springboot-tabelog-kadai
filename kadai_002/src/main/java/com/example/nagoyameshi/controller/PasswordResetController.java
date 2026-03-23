package com.example.nagoyameshi.controller;

import com.example.nagoyameshi.entity.PasswordResetToken;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.PasswordResetForm;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.service.PasswordResetTokenService;
import com.example.nagoyameshi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Controller
public class PasswordResetController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final JavaMailSender javaMailSender;

    public PasswordResetController(
            UserRepository userRepository,
            UserService userService,
            PasswordResetTokenService passwordResetTokenService,
            JavaMailSender javaMailSender) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.javaMailSender = javaMailSender;
    }

    @GetMapping("/password-reset/request")
    public String requestForm() {
        return "auth/password-reset-request";
    }

    @PostMapping("/password-reset/request")
    public String sendResetLink(
            @RequestParam("email") String email,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        userRepository.findByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            passwordResetTokenService.create(user, token);
            sendPasswordResetMail(user, buildResetUrl(request, token));
        });

        redirectAttributes.addFlashAttribute("successMessage", "入力されたメールアドレス宛にパスワード再設定用リンクを送信しました。メールをご確認ください。");
        return "redirect:/login";
    }

    @GetMapping("/password-reset")
    public String showResetForm(
            @RequestParam("token") String token,
            Model model,
            RedirectAttributes redirectAttributes) {

        PasswordResetToken passwordResetToken = passwordResetTokenService.getByToken(token);
        if (!passwordResetTokenService.isValid(passwordResetToken)) {
            redirectAttributes.addFlashAttribute("errorMessage", "パスワード再設定リンクが無効、または有効期限切れです。");
            return "redirect:/login";
        }

        model.addAttribute("token", token);
        model.addAttribute("passwordResetForm", new PasswordResetForm());
        return "auth/password-reset";
    }

    @PostMapping("/password-reset")
    public String resetPassword(
            @RequestParam("token") String token,
            @Valid @ModelAttribute PasswordResetForm passwordResetForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        PasswordResetToken passwordResetToken = passwordResetTokenService.getByToken(token);
        if (!passwordResetTokenService.isValid(passwordResetToken)) {
            redirectAttributes.addFlashAttribute("errorMessage", "パスワード再設定リンクが無効、または有効期限切れです。");
            return "redirect:/login";
        }

        if (!passwordResetForm.getPassword().equals(passwordResetForm.getPasswordConfirmation())) {
            bindingResult.rejectValue("passwordConfirmation", "password.mismatch", "確認用パスワードが一致しません。");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("token", token);
            return "auth/password-reset";
        }

        userService.updatePassword(passwordResetToken.getUser(), passwordResetForm.getPassword());
        passwordResetTokenService.markAsUsed(passwordResetToken);

        redirectAttributes.addFlashAttribute("successMessage", "パスワードを再設定しました。新しいパスワードでログインしてください。");
        return "redirect:/login";
    }

    private String buildResetUrl(HttpServletRequest request, String token) {
        return ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(request.getContextPath() + "/password-reset")
                .replaceQuery(null)
                .queryParam("token", token)
                .build()
                .toUriString();
    }

    private void sendPasswordResetMail(User user, String resetUrl) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom("springboot.nagoyameshi@example.com");
            helper.setTo(user.getEmail());
            helper.setSubject("パスワード再設定");

            String html = """
                    <p>以下のボタンを押してパスワードを再設定してください。</p>
                    <p><a href=\"%s\">パスワードを再設定する</a></p>
                    <p>ボタンが使えない場合は、次のURLをブラウザに貼り付けてください。</p>
                    <p>%s</p>
                    """.formatted(resetUrl, resetUrl);

            helper.setText(html, true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException | MailException e) {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("springboot.nagoyameshi@example.com");
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("パスワード再設定");
            mailMessage.setText("以下のリンクをクリックしてパスワードを再設定してください。\n\n" + resetUrl + "\n");
            javaMailSender.send(mailMessage);
        }
    }
}
