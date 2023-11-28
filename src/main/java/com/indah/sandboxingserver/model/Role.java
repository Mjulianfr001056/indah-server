package com.indah.sandboxingserver.model;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public enum Role {
    @Enumerated(EnumType.STRING)
    ADMIN,

    @Enumerated(EnumType.STRING)
    USER
}