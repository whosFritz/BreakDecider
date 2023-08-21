package com.whosfritz.breakdecider.Data.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Table(name = "stimmzettel", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"breakdecideruser_id", "abstimmungsthema_id"})
})
public class Stimmzettel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "stimmzettel_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "auswahl")
    private Entscheidung entscheidung;

    @Column(name = "stimmabgabedatum")
    private LocalDateTime stimmabgabedatum;

    @ManyToOne()
    @JoinColumn(name = "breakdecideruser_id")
    private BreakDeciderUser breakDeciderUser;

    @ManyToOne
    @JoinColumn(name = "abstimmungsthema_id")
    private Abstimmungsthema abstimmungsthema;


    @Override
    public String toString() {
        return "Stimmzettel(id=" + id + ", entscheidung=" + entscheidung + ")";
    }

}
