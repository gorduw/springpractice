package com.testing.springpractice.model;


import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
public class Advisor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer age;

    // private String email;
    // private String city;

}
