package uz.smartcode.smartapp.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import uz.smartcode.smartapp.controller.TagController;
import uz.smartcode.smartapp.entity.Tag;
import uz.smartcode.smartapp.service.impl.TagServiceImpl;

@RestController
@RequestMapping("/api/tag")
@CrossOrigin(origins = "*")
public class TagControllerImpl implements TagController {

    private final TagServiceImpl service;

    @Autowired
    public TagControllerImpl(TagServiceImpl service) {
        this.service = service;
    }

    @GetMapping
    @Override
    public ResponseEntity<?> getTags() {
        return service.getAll();
    }

    @GetMapping("/{tagId}")
    @Override
    public ResponseEntity<?> getTag(@PathVariable("tagId") Integer tagId) {
        return service.getOne(tagId);
    }

    @PostMapping
    @Override
    @Secured({"ROLE_SUPER_ADMIN", "ROLE_ADMIN"})
    public ResponseEntity<?> addTag(@RequestBody Tag tag) {
        return service.addTag(tag);
    }

    @PutMapping("/{tagId}")
    @Override
    @Secured({"ROLE_SUPER_ADMIN", "ROLE_ADMIN"})
    public ResponseEntity<?> editTag(@PathVariable("tagId") Integer tagId,@RequestBody Tag tag) {
        return service.editTag(tagId, tag);
    }

    @DeleteMapping("/{tagId}")
    @Override
    @Secured({"ROLE_SUPER_ADMIN", "ROLE_ADMIN"})
    public ResponseEntity<?> deleteTag(@PathVariable("tagId") Integer tagId) {
        return service.deleteTag(tagId);
    }
}
