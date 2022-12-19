package uz.smartcode.smartapp.service;

import org.springframework.http.ResponseEntity;
import uz.smartcode.smartapp.payload.CommentDto;

public interface CommentService {
    /**
     * @param page
     * @param size
     * */
    ResponseEntity<?> getAll(Integer page, Integer size);

    /**
     * @param commentId UUID
     * */
    ResponseEntity<?> getOneComment(Integer commentId);

    /**
     * @param dto {{@link CommentDto}}
     * */
    ResponseEntity<?> addComment(CommentDto dto);

    ResponseEntity<?> editComment(CommentDto dto);

    ResponseEntity<?> deleteComment(Integer commentId);
}
