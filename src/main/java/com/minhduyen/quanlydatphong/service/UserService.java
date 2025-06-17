package com.minhduyen.quanlydatphong.service;

import com.minhduyen.quanlydatphong.dto.ChangePasswordRequest;
import com.minhduyen.quanlydatphong.entity.User;
import com.minhduyen.quanlydatphong.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            String msg = messageSource.getMessage("user.change-password.wrong-current", null, LocaleContextHolder.getLocale());
            throw new IllegalStateException(msg);
        }
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            String msg = messageSource.getMessage("user.change-password.not-match", null, LocaleContextHolder.getLocale());
            throw new IllegalStateException(msg);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}