package uz.smartcode.smartapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import uz.smartcode.smartapp.entity.Role;
import uz.smartcode.smartapp.entity.User;
import uz.smartcode.smartapp.entity.enums.RoleName;
import uz.smartcode.smartapp.repository.RoleRepository;
import uz.smartcode.smartapp.repository.UserRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * @author Dilshod Fayzullayev <a href="fullstack.dev.uz@gmail.com">Email</a>
 * @version 1.0
 ***********/
@SpringBootApplication
public class SmartAppApplication implements CommandLineRunner {
    @Value("${smarapp.running.initialization.data}")
    private boolean initializationData;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SmartAppApplication(RoleRepository roleRepository, UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(SmartAppApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (initializationData) {
            List<Role> roles = roleRepository.saveAll(
                    Arrays.asList(
                            new Role(null, RoleName.ROLE_SUPER_ADMIN),
                            new Role(null, RoleName.ROLE_ADMIN),
                            new Role(null, RoleName.ROLE_USER)
                    )
            );
            User user = new User(null, "admin", "Dilshod", "Fayzullayev", "darkprohub-uz@yandex.ru", true);
            user.setPassword(passwordEncoder.encode("root123"));
            user.setRole(new HashSet<Role>(roles));
            userRepository.save(user);
        }
    }

}
