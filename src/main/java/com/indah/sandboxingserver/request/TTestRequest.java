package com.indah.sandboxingserver.request;

import lombok.Data;

@Data
public class TTestRequest {
    String tableId;
    String columnNames;
    TTestAlternative alternative;
    double mu = 0f;

    public enum TTestAlternative{
        LESS,
        GREATER,
        TWO_SIDED
    }
}
