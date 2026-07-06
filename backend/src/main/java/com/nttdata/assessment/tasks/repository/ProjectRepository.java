package com.nttdata.assessment.tasks.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nttdata.assessment.tasks.domain.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
