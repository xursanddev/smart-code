package uz.smartcode.smartapp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.smartcode.smartapp.entity.Attachment;
import uz.smartcode.smartapp.entity.AttachmentContent;
import uz.smartcode.smartapp.entity.Role;
import uz.smartcode.smartapp.entity.User;
import uz.smartcode.smartapp.entity.enums.RoleName;
import uz.smartcode.smartapp.payload.UserDto;
import uz.smartcode.smartapp.payload.response.MessageResponse;
import uz.smartcode.smartapp.payload.response.UserResponse;
import uz.smartcode.smartapp.repository.AttachmentContentRepository;
import uz.smartcode.smartapp.repository.RoleRepository;
import uz.smartcode.smartapp.repository.SocialRepository;
import uz.smartcode.smartapp.repository.UserRepository;
import uz.smartcode.smartapp.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final RoleRepository roleRepository;
    private final SocialRepository socialRepository;
    private final MailService mailService;

    private final AttachmentContentRepository contentRepository;

    @Autowired
    public UserServiceImpl(UserRepository repository, RoleRepository roleRepository, SocialRepository socialRepository, MailService mailService, AttachmentContentRepository contentRepository) {
        this.repository = repository;
        this.roleRepository = roleRepository;
        this.socialRepository = socialRepository;
        this.mailService = mailService;
        this.contentRepository = contentRepository;
    }

    @Override
    public ResponseEntity<?> getUsers(Integer page, Integer size, String sortBy) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? page - 1 : 0, size > 0 ? size : 10, Sort.by(sortBy));
        return ok(repository.findAll(pageRequest).map(UserResponse::new));
    }

    @Override
    public ResponseEntity<?> getUser(UUID userId) {
        Optional<User> optionalUser = repository.findById(userId);
        if (!optionalUser.isPresent()) return notFound().build();
        return ok(optionalUser.get());
    }

    @Override
    public ResponseEntity<?> getMe() {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ok(new UserResponse(principal));
    }

    @Override
    public ResponseEntity<?> addUser(UserDto dto) {
        if (!usernameIsValid(dto.getUsername()))
            return status(BAD_REQUEST).body(new MessageResponse(BAD_REQUEST, "Username entered incorrectly"));
        if (repository.existsByEmail(dto.getEmail())) return status(UNPROCESSABLE_ENTITY).build();
        if (repository.existsByUsername(dto.getUsername())) return status(UNPROCESSABLE_ENTITY).build();
        Role role = roleRepository.getRoleByName(RoleName.ROLE_USER);
        User user = new User(dto.getUsername(), dto.getFirstname(), dto.getLastname(), dto.getEmail(), Collections.singleton(role));
        user.setPassword(dto.getPassword());
        user.setEnabled(true);
        return status(CREATED).body(repository.saveAndFlush(user));
    }

    @Override
    public ResponseEntity<?> editUser(UUID userId, UserDto dto) {
        Optional<User> optionalUser = repository.findById(userId);
        if (!optionalUser.isPresent()) return notFound().build();
        if (repository.existsByEmailAndIdNot(dto.getEmail(), userId))
            return status(UNPROCESSABLE_ENTITY).build();
        if (repository.existsByUsernameAndIdNot(dto.getUsername(), userId))
            return status(UNPROCESSABLE_ENTITY).build();
        User user = optionalUser.get();
        user.setFirstName(dto.getFirstname());
        user.setLastName(dto.getLastname());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setBio(dto.getBio());
        return status(CREATED).body(repository.save(user));
    }

    @Override
    public ResponseEntity<?> deleteUser(UUID userId) {
        Optional<User> optionalUser = repository.findById(userId);
        if (!optionalUser.isPresent()) return notFound().build();
        try {
            repository.delete(optionalUser.get());
            return noContent().build();
        } catch (Exception e) {
            return badRequest().build();
        }
    }

    @Override
    public ResponseEntity<?> userDeactivate(UUID userId, boolean isLocked) {
        Optional<User> optionalUser = repository.findById(userId);
        if (!optionalUser.isPresent()) return notFound().build();
        User user = optionalUser.get();
        user.setAccountNonLocked(!isLocked);
        String result = user.isAccountNonLocked() ? "Hisobingiz faollashtirildi" : "Hisobingiz blocklandi";
        mailService.sendMail(user.getEmail(), "Hisob holati ogohlantirilishi", result);
        return status(CREATED).body(new UserResponse(repository.save(user)));
    }

    @Override
    public ResponseEntity<?> getBlockUsers() {
        List<User> blockUsers = repository.findAllByAccountNonLocked(false);
        return ok(blockUsers.stream().map(UserResponse::new));
    }

    @Override
    public ResponseEntity<?> uploadAvatar(UUID userId, MultipartFile file) {
        Optional<User> optionalUser = repository.findById(userId);
        if (!optionalUser.isPresent()) return status(NOT_FOUND).body("User not found");
        User user = optionalUser.get();
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (fileName.contains("..")) return badRequest().body("Filename contains invalid path sequence");
            contentRepository.delete(optionalUser.get().getAttachment().getContent());
            Attachment attachment = new Attachment(fileName, file.getContentType(), file.getSize());
            AttachmentContent content = new AttachmentContent();
            attachment.setContent(content);
            content.setBytes(file.getBytes());
            content.setAttachment(attachment);
            AttachmentContent savedAttachment = contentRepository.save(content);
            user.setAttachment(savedAttachment.getAttachment());
            return status(CREATED).body(new UserResponse(repository.save(user)));
        } catch (Exception e) {
            return badRequest().build();
        }
    }

    @Override
    public ResponseEntity<?> removeAvatar(UUID userId) {
        Optional<User> optionalUser = repository.findById(userId);
        if (!optionalUser.isPresent()) return status(NOT_FOUND).body(new MessageResponse(NOT_FOUND, "User not found"));
        System.out.println(optionalUser.get().getAttachment());
        if (optionalUser.get().getAttachment() != null) {
            try {
                contentRepository.delete(optionalUser.get().getAttachment().getContent());
            } catch (Exception e) {
                return badRequest().build();
            }
        }
        return noContent().build();
    }

    public boolean usernameIsValid(String username) {
        Pattern pattern = Pattern.compile("^[a-z0-9_]{3,60}$");
        return pattern.matcher(username.toLowerCase()).matches();
    }
}
