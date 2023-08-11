package com.whosfritz.breakdecider.Data.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "abstimmungsthema")
public class Abstimmungsthema {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "abstimmungsthema_id")
    private Long id;
    @Column(name = "ersteller")
    private String ersteller;
    @Column(name = "erstelldatum")
    private LocalDate erstelldatum;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(name = "titel", columnDefinition = "LONGTEXT")
    private String titel;
    @Column(name = "beschreibung", columnDefinition = "LONGTEXT")
    private String beschreibung;

    @OneToMany(mappedBy = "abstimmungsthema", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Stimmzettel> stimmzettelList;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private BreakDeciderUser user;

    public Abstimmungsthema(String ersteller,
                            LocalDate erstelldatum,
                            Status status,
                            String titel,
                            String beschreibung) {
        this.ersteller = ersteller;
        this.erstelldatum = erstelldatum;
        this.status = status;
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.stimmzettelList = new ArrayList<>(); // Initialize the list
    }

}
