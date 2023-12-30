package com.indah.sandboxingserver.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MannWhitneyTestResponse {
    private double W;
    private double pValue;
}
