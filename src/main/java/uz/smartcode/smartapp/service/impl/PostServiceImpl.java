package uz.smartcode.smartapp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uz.smartcode.smartapp.entity.Post;
import uz.smartcode.smartapp.entity.Tag;
import uz.smartcode.smartapp.entity.User;
import uz.smartcode.smartapp.payload.PostDto;
import uz.smartcode.smartapp.payload.response.MessageResponse;
import uz.smartcode.smartapp.payload.response.PostResponse;
import uz.smartcode.smartapp.repository.PostRepository;
import uz.smartcode.smartapp.repository.TagRepository;
import uz.smartcode.smartapp.repository.UserRepository;
import uz.smartcode.smartapp.service.PostService;

import java.util.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.ResponseEntity.*;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository repository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    @Autowired
    public PostServiceImpl(PostRepository repository, UserRepository userRepository, TagRepository tagRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public ResponseEntity<?> getAllPosts(Integer page, Integer size, String sortBy) {
        PageRequest pageRequest = PageRequest.of(page > 0 ? page : 0, size > 0 ? size : 10, Sort.by(sortBy).descending());
//        return ok(repository.findAll(pageRequest).map(PostResponse::new));
        return ok(repository.findAll(pageRequest).map(post -> post.isPublished() ? new PostResponse(post) : null));
    }

    @Override
    public ResponseEntity<?> getUserPosts(UUID userId, String sortBy) {
        if (!userRepository.existsById(userId)) return notFound().build();
        List<Post> posts = repository.findAllByAuthor_Id(userId);
        return ok(posts.stream().map(PostResponse::new));
    }

    @Override
    public ResponseEntity<?> getPost(UUID postId) {
        Optional<Post> optionalPost = repository.findById(postId);
        if (!optionalPost.isPresent()) return notFound().build();
        return ok(new PostResponse(optionalPost.get()));
    }

    @Override
    public ResponseEntity<?> addPost(PostDto dto) {
        Optional<User> optionalUser = userRepository.findById(dto.getUserId());
        if (!optionalUser.isPresent())
            return status(NOT_FOUND).body("Unfortunately, the user with this id does not exist");
        Set<Tag> tags = tagsChecker(dto.getTags());
        if (tags.size() == 0) return status(NOT_FOUND).body("An existing valid tag was not found");
        Post savedPost = repository.save(new Post(dto.getTitle(), dto.getContent(), optionalUser.get(), tags));
        return status(CREATED).body(new PostResponse(savedPost));
    }

    //    TODO EDIT_POST
    @Override
    public ResponseEntity<?> editPost(UUID postId, PostDto dto) {
        Optional<Post> optionalPost = repository.findById(postId);
        if (!optionalPost.isPresent()) return notFound().build();
        Post post = optionalPost.get();
        Set<Tag> tags = tagsChecker(dto.getTags());
        if (tags.size() == 0) return status(NOT_FOUND).body("An existing valid tag was not found");
        post.setTags(tags);
        post.setContent(dto.getContent());
        post.setTitle(dto.getTitle());
        return status(CREATED).body(new PostResponse(post));
    }

    @Override
    public ResponseEntity<?> deletePost(UUID postId) {
        Optional<Post> optionalPost = repository.findById(postId);
        if (!optionalPost.isPresent()) return status(NOT_FOUND).body("Unfortunately, this id post was not found");
        try {
            repository.delete(optionalPost.get());
            return noContent().build();
        } catch (Exception e) {
            return badRequest().build();
        }
    }

    public ResponseEntity<?> publishPost(UUID userId, UUID postId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) return status(NOT_FOUND).body(new MessageResponse(NOT_FOUND, "User not found"));
        Optional<Post> optionalPost = repository.findById(postId);
        if (!optionalPost.isPresent()) return status(NOT_FOUND).body(new MessageResponse(NOT_FOUND, "Post not found"));
        Post post = optionalPost.get();
        post.setPublished(true);
        post.setPublishedAt(new Date());
        return ok(repository.save(post));
    }

    //    POST ACTIONS
    public Set<Tag> tagsChecker(Set<Integer> tagsId) {
        Set<Tag> tags = new HashSet<>();
        for (Integer tagId : tagsId) {
            Optional<Tag> optionalTag = tagRepository.findById(tagId);
            optionalTag.ifPresent(tags::add);
        }
        return tags;
    }
}
