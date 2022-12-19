package uz.smartcode.smartapp.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.smartcode.smartapp.controller.UserController;
import uz.smartcode.smartapp.payload.UserDto;
import uz.smartcode.smartapp.service.impl.UserServiceImpl;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserControllerImpl implements UserController {
    private final UserServiceImpl service;

    @Autowired
    public UserControllerImpl(UserServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    @Override
    @Secured({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    public ResponseEntity<?> getAll(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                    @RequestParam(name = "size", defaultValue = "10") Integer size,
                                    @RequestParam(name = "sort", defaultValue = "id") String sortBy) {
        return service.getUsers(page, size, sortBy);
    }

    @GetMapping("/{userId}")
    @Override
    public ResponseEntity<?> getOne(@PathVariable UUID userId) {
        return service.getUser(userId);
    }

    @PostMapping
    @Override
    @Secured({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    public ResponseEntity<?> saveUser(@Valid @RequestBody UserDto dto) {
        return service.addUser(dto);
    }

    @PutMapping("/{userId}")
    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUser(@PathVariable UUID userId, @RequestBody UserDto dto) {
        return service.editUser(userId, dto);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMe() {
        return service.getMe();
    }

    @DeleteMapping("/{userId}")
    @Override
    @Secured({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    public ResponseEntity<?> deleteUser(@PathVariable UUID userId) {
        return service.deleteUser(userId);
    }

    @PostMapping("/{userId}/block/{isLocked}")
    @Override
    @Secured({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    public ResponseEntity<?> blockUser(@PathVariable("userId") UUID userId,@PathVariable("isLocked") boolean status) {
        return service.userDeactivate(userId, status);
    }

    @GetMapping("/block")
    @Override
    @Secured({"ROLE_ADMIN", "ROLE_SUPER_ADMIN"})
    public ResponseEntity<?> getBlockUsers() {
        return service.getBlockUsers();
    }

    @PostMapping("upload/avatar/{userId}")
    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadAvatar(@PathVariable("userId") UUID userId, @RequestParam("file") MultipartFile file) {
        return service.uploadAvatar(userId, file);
    }
}
