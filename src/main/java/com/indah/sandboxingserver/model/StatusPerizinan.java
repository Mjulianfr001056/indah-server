package com.indah.sandboxingserver.model;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public enum StatusPerizinan {
    @Enumerated(EnumType.STRING)
    DISETUJUI,

    @Enumerated(EnumType.STRING)
    PENDING,

    @Enumerated(EnumType.STRING)
    DITOLAK
}
