package com.indah.sandboxingserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class KatalogData {
    private String id;
    private String judul;
    private String kategori;
    private String tahun;
}
