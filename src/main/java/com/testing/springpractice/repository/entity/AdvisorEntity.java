package com.testing.springpractice.repository.entity;


import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name="advisor")
public class AdvisorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer age;

}
