package com.whosfritz.breakdecider.Data.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
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
    private Set<Stimmzettel> stimmzettelSet = new HashSet<>();


}
