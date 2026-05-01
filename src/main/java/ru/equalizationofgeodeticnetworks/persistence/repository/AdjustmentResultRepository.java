package ru.equalizationofgeodeticnetworks.persistence.repository;

import ru.equalizationofgeodeticnetworks.persistence.entity.AdjustmentResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdjustmentResultRepository extends JpaRepository<AdjustmentResult, Long> {
    List<AdjustmentResult> findByProjectIdOrderByCreatedAtDesc(Long projectId);
}
