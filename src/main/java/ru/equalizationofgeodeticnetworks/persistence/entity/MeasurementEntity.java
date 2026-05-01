package ru.equalizationofgeodeticnetworks.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "measurement")
@Getter @Setter
public class MeasurementEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne @JoinColumn(name = "project_id")
    private Project project;
    private String type;
    @Column(columnDefinition = "jsonb")
    private String data;
    private Double sigma;
    private Double weight;
}
