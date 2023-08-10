package com.whosfritz.breakdecider.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String erstelldatum;
    @Column(name = "status")
    private String status;
    @Column(name = "titel", columnDefinition = "LONGTEXT")
    private String titel;
    @Column(name = "beschreibung", columnDefinition = "LONGTEXT")
    private String beschreibung;
    @Column(name = "ja_stimmen")
    private int jaStimmen;
    @Column(name = "nein_stimmen")
    private int neinStimmen;

//
//    @OneToMany(mappedBy = "abstimmungsthema")
//    private List<Stimmzettel> stimmzettel;

    public Abstimmungsthema(String ersteller, String erstellerdatum, String status, String titel, String beschreibung, int jaStimmen, int neinStimmen) {
        this.ersteller = ersteller;
        this.erstelldatum = erstellerdatum;
        this.status = status;
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.jaStimmen = jaStimmen;
        this.neinStimmen = neinStimmen;
    }
}
