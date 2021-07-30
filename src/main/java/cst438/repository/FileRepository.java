package cst438.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cst438.domain.File;

public interface FileRepository  extends JpaRepository<File,Long> {
    List<File> findByBoardId(String boardId);
    List<File> findByNameAndBoardId(String name, String boardId);
}
