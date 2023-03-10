package uz.smartcode.smartapp.service;

import org.springframework.http.ResponseEntity;
import uz.smartcode.smartapp.entity.Social;
import uz.smartcode.smartapp.payload.SocialDto;

import java.util.UUID;

public interface SocialService {
    ResponseEntity<?> getAll(UUID userId);
    ResponseEntity<?> getOne(Integer id);
    ResponseEntity<?> addSocial(SocialDto dto);
    ResponseEntity<?> editSocial(Integer id, Social social);
    ResponseEntity<?> deleteSocial(Integer id);
}
