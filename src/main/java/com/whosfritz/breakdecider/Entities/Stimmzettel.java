package com.whosfritz.breakdecider.Entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
@Table(name = "stimmzettel")
public class Stimmzettel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "stimmzettel_id")
    private Long id;
    @Column(name = "auswahl")
    private String auswahl;
    @Column(name = "abstimmer")
    private String abstimmer;
//
//    @ManyToOne
//    @JoinColumn(name = "abstimmungsthema_id")
//    private Abstimmungsthema abstimmungsthema;
//
}