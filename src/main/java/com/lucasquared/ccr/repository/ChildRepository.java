package com.lucasquared.ccr.repository;

import com.lucasquared.ccr.domain.child.Child;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildRepository extends JpaRepository<Child, String> {
}
