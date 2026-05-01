package ru.equalizationofgeodeticnetworks.persistence.service;

import ru.equalizationofgeodeticnetworks.persistence.entity.Project;
import ru.equalizationofgeodeticnetworks.persistence.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository repository;

    public Project findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public Project save(Project project) {
        return repository.save(project);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
