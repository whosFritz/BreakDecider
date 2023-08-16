package com.whosfritz.breakdecider.Data.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@AllArgsConstructor
@Data
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
    private LocalDate stimmabgabedatum;

    @ManyToOne()
    @JoinColumn(name = "breakdecideruser_id")
    private BreakDeciderUser breakDeciderUser;

    @ManyToOne
    @JoinColumn(name = "abstimmungsthema_id")
    private Abstimmungsthema abstimmungsthema;

    public Stimmzettel(Entscheidung entscheidung,
                       LocalDate stimmabgabedatum,
                       BreakDeciderUser breakDeciderUser,
                       Abstimmungsthema abstimmungsthema) {
        this.entscheidung = entscheidung;
        this.stimmabgabedatum = stimmabgabedatum;
        this.breakDeciderUser = breakDeciderUser;
        this.abstimmungsthema = abstimmungsthema;
    }
}
