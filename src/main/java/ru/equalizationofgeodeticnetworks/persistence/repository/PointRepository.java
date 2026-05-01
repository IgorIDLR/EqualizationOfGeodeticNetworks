package ru.equalizationofgeodeticnetworks.persistence.repository;

import ru.equalizationofgeodeticnetworks.persistence.entity.PointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PointRepository extends JpaRepository<PointEntity, Long> {
    List<PointEntity> findByProjectId(Long projectId);
}
