package com.factoreal.backend.repository;

import com.factoreal.backend.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkerRepository extends JpaRepository<Worker, String> {
}
