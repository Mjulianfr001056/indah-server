package com.indah.sandboxingserver.model;

import javax.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "katalog_data")
public class KatalogData {
    @Id
    private String id;

    @Column(nullable = false)
    private String judul;

    @Column
    private String kategori;

    @Column(nullable = false)
    private Integer tahun;
}
