package com.example.myrecipai.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "follows")
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    private User following;
    @ManyToOne
    @JoinColumn(name = "followed_id", nullable = false)
    private User followed;
    @Column(name = "date_created")
    private Date dateCreated;
}