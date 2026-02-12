package com.lucasquared.ccr.repository;

import com.lucasquared.ccr.domain.child.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChildRepository extends JpaRepository<Child, String> {
    List<Child> findByNameContainingIgnoreCase(String name);
}
