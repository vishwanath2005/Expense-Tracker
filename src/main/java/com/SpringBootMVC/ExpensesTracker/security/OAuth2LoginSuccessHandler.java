package com.SpringBootMVC.ExpensesTracker.security;

import com.SpringBootMVC.ExpensesTracker.entity.Client;
import com.SpringBootMVC.ExpensesTracker.entity.User;
import com.SpringBootMVC.ExpensesTracker.service.ClientService;
import com.SpringBootMVC.ExpensesTracker.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final ClientService clientService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public OAuth2LoginSuccessHandler(UserService userService, ClientService clientService,
            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.clientService = clientService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String email = token.getPrincipal().getAttribute("email");
        String name = token.getPrincipal().getAttribute("name");

        User user = userService.findUserByUserName(email);

        if (user == null) {
            // Register new user
            user = new User();
            user.setUserName(email);
            // Verify if your User entity requires a password. If so, generate a dummy one.
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            // user.setEnabled(true); // Check if you have an enabled flag
            userService.save(user);

            // Create Client profile
            Client client = new Client();
            client.setId(user.getId());
            client.setId(user.getId());
            if (name != null) {
                String[] parts = name.split(" ", 2);
                client.setFirstName(parts[0]);
                client.setLastName(parts.length > 1 ? parts[1] : "");
            } else {
                client.setFirstName("Google");
                client.setLastName("User");
            }
            client.setEmail(email);
            clientService.saveClient(client);
        }

        // Add to Session
        Client client = clientService.findClientById(user.getId());
        HttpSession session = request.getSession();
        session.setAttribute("client", client);
        session.setAttribute("user", user);

        super.setDefaultTargetUrl("/list");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
