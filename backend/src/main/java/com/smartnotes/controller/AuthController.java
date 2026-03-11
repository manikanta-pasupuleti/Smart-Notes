package com.smartnotes.controller;

import com.smartnotes.model.User;
import com.smartnotes.repository.UserRepository;
import com.smartnotes.security.JwtTokenProvider;
import com.smartnotes.security.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "Username already exists");
            }});
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "Email already exists");
            }});
        }

        // Hash password
        user.setPassword(PasswordUtil.hashPassword(user.getPassword()));

        // Save user
        User savedUser = userRepository.save(user);

        // Generate token
        String token = jwtTokenProvider.generateToken(user.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("id", savedUser.getId());
        response.put("username", savedUser.getUsername());
        response.put("email", savedUser.getEmail());
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        // Find user by username
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "User not found");
            }});
        }

        User user = userOptional.get();

        // Verify password
        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "Invalid password");
            }});
        }

        // Generate token
        String token = jwtTokenProvider.generateToken(user.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("valid", "true");
                put("username", username);
            }});
        }

        return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
            put("valid", "false");
        }});
    }
}


        Map<String, Object> response = new HashMap<>();
        response.put("id", savedUser.getId());
        response.put("username", savedUser.getUsername());
        response.put("email", savedUser.getEmail());
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        // Find user by username
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "User not found");
            }});
        }

        User user = userOptional.get();

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
                put("error", "Invalid password");
            }});
        }

        // Generate token
        String token = jwtTokenProvider.generateToken(user.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("valid", "true");
                put("username", username);
            }});
        }

        return ResponseEntity.badRequest().body(new HashMap<String, String>() {{
            put("valid", "false");
        }});
    }
}
