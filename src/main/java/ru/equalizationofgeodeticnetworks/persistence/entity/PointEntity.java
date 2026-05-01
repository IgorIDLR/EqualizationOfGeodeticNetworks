package ru.equalizationofgeodeticnetworks.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "point")
@Getter @Setter
public class PointEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne @JoinColumn(name = "project_id")
    private Project project;
    private String name;
    private String type; // PLANAR, LEVEL
    private Double x;
    private Double y;
    private Double height;
    private boolean isFixed;
}
