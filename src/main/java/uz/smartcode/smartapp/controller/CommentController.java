package uz.smartcode.smartapp.controller;

import org.springframework.http.ResponseEntity;
import uz.smartcode.smartapp.payload.CommentDto;

import java.util.UUID;

public interface CommentController {
    ResponseEntity<?> getComment(UUID commentId);

    ResponseEntity<?> updateComment(UUID commentId, CommentDto commentDto);

    ResponseEntity<?> deleteComment(UUID commentId);
}
