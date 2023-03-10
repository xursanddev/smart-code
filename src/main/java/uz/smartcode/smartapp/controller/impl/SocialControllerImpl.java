package uz.smartcode.smartapp.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.smartcode.smartapp.controller.SocialController;
import uz.smartcode.smartapp.entity.Social;
import uz.smartcode.smartapp.payload.SocialDto;
import uz.smartcode.smartapp.service.impl.SocialServiceImpl;

import java.util.UUID;

@RestController
@RequestMapping("/api/social")
@CrossOrigin(origins = "*")
public class SocialControllerImpl implements SocialController {
    private final SocialServiceImpl service;

    @Autowired
    public SocialControllerImpl(SocialServiceImpl service) {
        this.service = service;
    }

    @GetMapping(path = "/user/{userId}")
    @Override
    public ResponseEntity<?> getSocials(@PathVariable UUID userId) {
        return service.getAll(userId);
    }

    @GetMapping("/{socialId}")
    @Override
    public ResponseEntity<?> getSocial(@PathVariable Integer socialId) {
        return service.getOne(socialId);
    }

    @PostMapping
    @Override
    @Secured({"ROLE_SUPER_ADMIN", "ROLE_ADMIN"})
    public ResponseEntity<?> addSocials(@RequestBody SocialDto socialDto) {
        return service.addSocial(socialDto);
    }

    @PutMapping("/{socialId}")
    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> editSocial(@PathVariable Integer socialId, Social social) {
        return service.editSocial(socialId, social);
    }

    @DeleteMapping("/{socialId}")
    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteSocial(@PathVariable Integer socialId) {
        return service.deleteSocial(socialId);
    }
}
