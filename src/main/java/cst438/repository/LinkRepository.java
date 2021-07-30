package cst438.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cst438.domain.Link;

public interface LinkRepository extends JpaRepository<Link, String> {
	public List<Link> findByUserIdAndDate(Long userId, Date date);
	public List<Link> findByUserId(Long userId);
}