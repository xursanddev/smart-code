package uz.smartcode.smartapp.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.smartcode.smartapp.controller.LevelController;
import uz.smartcode.smartapp.entity.Level;
import uz.smartcode.smartapp.service.impl.LevelServiceImpl;

@RestController
@RequestMapping("/api/level")
@CrossOrigin(origins = "*")
public class LevelControllerImpl implements LevelController {

    private final LevelServiceImpl service;

    @Autowired
    public LevelControllerImpl(LevelServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    @Override
    public ResponseEntity<?> getLevels() {
        return service.getAll();
    }

    @GetMapping("/{levelId}")
    @Override
    public ResponseEntity<?> getLevel(@PathVariable Integer levelId) {
        return service.getOne(levelId);
    }

    @PostMapping
    @Override
    @Secured({"ROLE_SUPER_ADMIN", "ROLE_ADMIN"})
    public ResponseEntity<?> addLevel(@RequestBody Level level) {
        return service.addLevel(level);
    }

    @PutMapping("/{levelId}")
    @Override
    @Secured({"ROLE_SUPER_ADMIN", "ROLE_ADMIN"})
    public ResponseEntity<?> editLevel(@PathVariable Integer levelId, @RequestBody Level level) {
        return service.editLevel(levelId, level);
    }

    @DeleteMapping("/{levelId}")
    @Override
    @Secured({"ROLE_SUPER_ADMIN", "ROLE_ADMIN"})
    public ResponseEntity<?> deleteLevel(@PathVariable Integer levelId) {
        return service.deleteLevel(levelId);
    }
}
