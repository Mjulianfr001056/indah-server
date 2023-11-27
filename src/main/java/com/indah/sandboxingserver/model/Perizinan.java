package com.indah.sandboxingserver.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "perizinan")
public class Perizinan {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id", nullable = false)
    private User user;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date date;

    @ManyToOne
    @JoinColumn(name="nama_data", referencedColumnName = "id", nullable = false)
    private KatalogData namaData;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPerizinan statusPerizinan;
}