package uz.smartcode.smartapp.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uz.smartcode.smartapp.payload.UserDto;

import java.util.UUID;

public interface UserService {
    ResponseEntity<?> getUsers(Integer page, Integer size, String sortBy);

    ResponseEntity<?> getUser(UUID userId);

    ResponseEntity<?> getMe();

    ResponseEntity<?> addUser(UserDto dto);

    ResponseEntity<?> editUser(UUID userId, UserDto dto);

    ResponseEntity<?> deleteUser(UUID userId);

    /**
     * Only admin use
     * */
    ResponseEntity<?> userDeactivate(UUID userId, boolean status);

    ResponseEntity<?> getBlockUsers();

    ResponseEntity<?> uploadAvatar(UUID userId, MultipartFile file);

    ResponseEntity<?> removeAvatar(UUID userId);
}
