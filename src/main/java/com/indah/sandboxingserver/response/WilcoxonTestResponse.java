package com.indah.sandboxingserver.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WilcoxonTestResponse {
    private double V;
    private double pValue;
}
