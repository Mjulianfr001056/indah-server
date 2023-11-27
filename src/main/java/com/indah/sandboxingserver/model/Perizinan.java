package com.indah.sandboxingserver.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@AllArgsConstructor
public class Perizinan {
    private String id;
    private User user;
    private Date date;
    private String namaData;
    private StatusPerizinan statusPerizinan;
}
