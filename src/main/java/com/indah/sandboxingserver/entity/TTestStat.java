package com.indah.sandboxingserver.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TTestStat {
    private double t;
    private double p;
    private double df;
    private double mean;
    private double sd;
    private double ci95Low;
    private double ci95High;
}
