package uz.smartcode.smartapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.smartcode.smartapp.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
}