package com.vit.security;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpSession;

// adds the logged in user to every page (navbar shows it)
@ControllerAdvice
public class UserAdvice {

    @ModelAttribute
    public void addUser(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (user instanceof UserInfo) {
            UserInfo u = (UserInfo) user;
            model.addAttribute("userName", u.getUsername());
            model.addAttribute("role", u.getRole());
        }
    }
}
