package ru.equalizationofgeodeticnetworks.persistence.repository;

import ru.equalizationofgeodeticnetworks.persistence.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> { }