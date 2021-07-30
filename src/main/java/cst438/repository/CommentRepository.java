package cst438.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cst438.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment,Long> {
	List<Comment> findByFileId(Long fileId);
}