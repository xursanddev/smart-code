package uz.smartcode.smartapp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.smartcode.smartapp.entity.Comment;
import uz.smartcode.smartapp.entity.Post;
import uz.smartcode.smartapp.payload.CommentDto;
import uz.smartcode.smartapp.repository.CommentRepository;
import uz.smartcode.smartapp.repository.PostRepository;
import uz.smartcode.smartapp.service.CommentService;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.springframework.http.ResponseEntity.*;

//TODO COMMENT
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository repository;
    private final PostRepository postRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository repository, PostRepository postRepository) {
        this.repository = repository;
        this.postRepository = postRepository;
    }

    @Override
    public ResponseEntity<?> getAll(Integer page, Integer size) {
        return null;
    }

    @Override
    public ResponseEntity<?> getOneComment(Integer commentId) {
        Optional<Comment> optionalComment = repository.findById(commentId);
        if (!optionalComment.isPresent()) return notFound().build();
        return ok(optionalComment.get());
    }

    @Override
    public ResponseEntity<?> addComment(CommentDto dto) {
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        if (dto.getPostId() == null && dto.getCommentId() == null) return badRequest().build();
        if (dto.getCommentId() != null) {
            Optional<Comment> optionalComment = repository.findById(dto.getCommentId());
            if (!optionalComment.isPresent()) return notFound().build();
            Comment parent = optionalComment.get();
            Set<Comment> child = parent.getChild();
            child.add(comment);
            return status(HttpStatus.CREATED).body(repository.save(parent));
        }
        if (dto.getPostId() != null) {
            Optional<Post> optionalPost = postRepository.findById(dto.getPostId());
            if (!optionalPost.isPresent()) return notFound().build();
            comment.setPost(optionalPost.get());
            return status(HttpStatus.CREATED).body(repository.save(comment));
        }
        return badRequest().body("bad content");
    }

    @Override
    public ResponseEntity<?> editComment(CommentDto dto) {
        return null;
    }

    @Override
    public ResponseEntity<?> deleteComment(Integer commentId) {
        Optional<Comment> optionalComment = repository.findById(commentId);
        if (!optionalComment.isPresent()) return notFound().build();
        try {
            repository.delete(optionalComment.get());
            return noContent().build();
        } catch (Exception e) {
            return badRequest().build();
        }
    }
}
