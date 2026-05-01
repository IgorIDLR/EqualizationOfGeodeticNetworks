package ru.equalizationofgeodeticnetworks.persistence.repository;

import ru.equalizationofgeodeticnetworks.persistence.entity.MeasurementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MeasurementRepository extends JpaRepository<MeasurementEntity, Long> {
    List<MeasurementEntity> findByProjectId(Long projectId);
}
