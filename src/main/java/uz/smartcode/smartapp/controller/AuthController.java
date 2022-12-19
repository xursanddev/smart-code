package uz.smartcode.smartapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.smartcode.smartapp.payload.request.LoginRequest;
import uz.smartcode.smartapp.payload.request.RestorePasswordRequest;
import uz.smartcode.smartapp.payload.request.SignUpRequest;
import uz.smartcode.smartapp.payload.request.VerifyEmailRequest;
import uz.smartcode.smartapp.service.AuthService;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return authService.signIn(request);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody SignUpRequest request) {
        return authService.signUp(request);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@Valid @RequestBody VerifyEmailRequest request) {
        return authService.verifyEmail(request);
    }

    @GetMapping("/verify/sendCode/{email}")
    public ResponseEntity<?> sendVerifyCode(@PathVariable("email") String email) {
        return authService.verifyCodeSendEmail(email);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody RestorePasswordRequest request) {
        return ResponseEntity.ok("restore-password");
    }
}
