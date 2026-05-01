package ru.equalizationofgeodeticnetworks.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "adjustment_result")
@Getter @Setter
public class AdjustmentResult {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne @JoinColumn(name = "project_id")
    private Project project;
    @Column(columnDefinition = "jsonb")
    private String coordinates;
    @Column(columnDefinition = "jsonb")
    private String covariance;
    private Double s0;
    private LocalDateTime createdAt;

    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}
