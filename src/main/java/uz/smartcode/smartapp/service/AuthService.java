package uz.smartcode.smartapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.smartcode.smartapp.components.jwt.JwtTokenProvider;
import uz.smartcode.smartapp.entity.Role;
import uz.smartcode.smartapp.entity.User;
import uz.smartcode.smartapp.entity.enums.RoleName;
import uz.smartcode.smartapp.payload.request.LoginRequest;
import uz.smartcode.smartapp.payload.request.SignUpRequest;
import uz.smartcode.smartapp.payload.request.VerifyEmailRequest;
import uz.smartcode.smartapp.payload.response.JwtResponse;
import uz.smartcode.smartapp.payload.response.MessageResponse;
import uz.smartcode.smartapp.repository.RoleRepository;
import uz.smartcode.smartapp.repository.UserRepository;
import uz.smartcode.smartapp.service.impl.MailService;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.ResponseEntity.*;

/***********
 * @author Dilshod Fayzullayev <a href="fullstack.dev.uz@gmail.com">Email</a>
 * @version 1.0
 ***********/
@Service
public class AuthService implements UserDetailsService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MailService mailService;

    @Autowired
    public AuthService(@Lazy AuthenticationManager authenticationManager, @Lazy PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider, UserRepository userRepository, RoleRepository roleRepository, MailService mailService) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.mailService = mailService;
    }

    //************************************************************************//
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));
    }
    //************************************************************************//

    public ResponseEntity<?> signIn(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername().toLowerCase(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        User user = (User) authentication.getPrincipal();
        return ok(
                new JwtResponse(token, user.getId(), user.getUsername(), user.getEmail(), user.getRole())
        );
    }

    public ResponseEntity<?> signUp(SignUpRequest request) {
        if (!usernameIsValid(request.getUsername())) return status(BAD_REQUEST).body(new MessageResponse(BAD_REQUEST, "Username entered incorrectly"));
        if (userRepository.existsByUsername(request.getUsername()))
            return badRequest().body(new MessageResponse(BAD_REQUEST, "Username is already taken!"));
        if (userRepository.existsByEmail(request.getEmail()))
            return badRequest().body(new MessageResponse(BAD_REQUEST, "Email is already in use!"));
        Set<Role> roleSet = Collections.singleton(roleRepository.getRoleByName(RoleName.ROLE_USER));
        User user = new User(null, request.getUsername().toLowerCase(), request.getFirstname(), request.getLastname(), request.getEmail(), false);
        user.setRole(roleSet);
        user.setActivationCode(generateVerifyCode());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User save = userRepository.save(user);
        mailService.sendMail(request.getEmail(), "Faollashtirish xabari", save.getActivationCode());
        return status(CREATED).body(new MessageResponse(CREATED, "User registered successfully!"));
    }

    public ResponseEntity<?> verifyEmail(VerifyEmailRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (!optionalUser.isPresent()) return status(NOT_FOUND).body(new MessageResponse(NOT_FOUND, "User not found"));
        User user = optionalUser.get();
        if (user.getActivationCode().equals(request.getCode())) {
            user.setEnabled(true);
            user.setActivationCode(null);
            userRepository.save(user);
            return ok(new MessageResponse(OK, "Your account has been successfully activated"));
        }
        return badRequest().build();
    }

    public ResponseEntity<?> verifyCodeSendEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            if (optionalUser.get().isEnabled()) return status(ACCEPTED).body("The user is already activated");
        }
        return status(NOT_FOUND).body(new MessageResponse(NOT_FOUND, "No email information was found"));
    }

    //************************************************************************//

    /**
     * @return {@link String}
     */
    public String generateVerifyCode() {
        return String.valueOf((int) (Math.random() * (999999 - 100000 + 1)) + 100000);
    }

    public boolean usernameIsValid(String username) {
        Pattern pattern = Pattern.compile("^[a-z0-9_]{3,60}$");
        return pattern.matcher(username.toLowerCase()).matches();
    }
}
