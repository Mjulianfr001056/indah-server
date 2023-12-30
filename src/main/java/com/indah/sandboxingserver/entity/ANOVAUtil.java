package com.indah.sandboxingserver.entity;

import lombok.Getter;
import org.apache.commons.math3.stat.inference.TestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.apache.commons.math3.stat.StatUtils.*;

public class ANOVAUtil {
    private Collection<double[]> data = new ArrayList<>();
    private DataSummary dataSummary;
    private BetweenGroups betweenGroups;
    private WithinGroups withinGroups;
    private Total total;

    public ANOVAUtil() {
        betweenGroups = new BetweenGroups();
        withinGroups = new WithinGroups();
        total = new Total();
    }

    @Getter
    public static class ANOVAStat{
        private final BetweenGroups betweenGroups;
        private final WithinGroups withinGroups;
        private final Total total;

        private ANOVAStat(BetweenGroups betweenGroups, WithinGroups withinGroups, Total total){
            this.betweenGroups = betweenGroups;
            this.withinGroups = withinGroups;
            this.total = total;
        }

        public String toJSON(){
            return "{" +
                    "\"betweenGroups\":{" +
                    "\"SS\":" + betweenGroups.SS +
                    ",\"df\":" + betweenGroups.df +
                    ",\"MS\":" + betweenGroups.MS +
                    ",\"F\":" + betweenGroups.F +
                    ",\"P\":" + betweenGroups.P +
                    "},\"withinGroups\":{" +
                    "\"SS\":" + withinGroups.SS +
                    ",\"df\":" + withinGroups.df +
                    ",\"MS\":" + withinGroups.MS +
                    "},\"total\":{" +
                    "\"SS\":" + total.SS +
                    ",\"df\":" + total.df +
                    "}}";
        }

    }

    private static class DataSummary {
        int numOfGroups;
        int numOfObservations;
        double sumOfObservations;
    }

    private static class BetweenGroups {
        double SS;
        int df;
        double MS;
        double F;
        double P;

        public BetweenGroups() {
            SS = 0;
            df = 0;
            MS = 0;
            F = 0;
            P = 0;
        }
    }

    private static class WithinGroups {
        double SS;
        int df;
        double MS;

        public WithinGroups() {
            SS = 0;
            df = 0;
            MS = 0;
        }
    }

    private static class Total {
        double SS;
        int df;

        public Total() {
            SS = 0;
            df = 0;
        }
    }

    public void addData(double[] data) {
        this.data.add(data);
    }

    private void summary() {
        dataSummary = new DataSummary();
        dataSummary.numOfGroups = data.size();
        dataSummary.numOfObservations = data.stream().mapToInt(group -> group.length).sum();
        dataSummary.sumOfObservations = data.stream().flatMapToDouble(Arrays::stream).sum();
    }

    private double sumSquaredMeans() {
        double grandMean = dataSummary.sumOfObservations / dataSummary.numOfObservations;
        return data.stream().mapToDouble(group -> group.length * Math.pow(sum(group) / group.length - grandMean, 2)).sum();
    }

    private double sumSquaredDeviations() {
        return data.stream().flatMapToDouble(group -> java.util.Arrays.stream(group).map(value -> Math.pow(value - sum(group) / group.length, 2))).sum();
    }

    public ANOVAStat calculate() {
        summary();

        int dfBetweenGroups = dataSummary.numOfGroups - 1;
        int dfWithinGroups = dataSummary.numOfObservations - dataSummary.numOfGroups;
        int dfTotal = dataSummary.numOfObservations - 1;

        double ssBetweenGroups = sumSquaredMeans();
        double ssWithinGroups = sumSquaredDeviations();
        double ssTotal = ssBetweenGroups + ssWithinGroups;

        double msBetweenGroups = ssBetweenGroups / dfBetweenGroups;
        double msWithinGroups = ssWithinGroups / dfWithinGroups;

        double pValue = TestUtils.oneWayAnovaPValue(data);
        double fValue = TestUtils.oneWayAnovaFValue(data);

        betweenGroups.SS = ssBetweenGroups;
        betweenGroups.df = dfBetweenGroups;
        betweenGroups.MS = msBetweenGroups;
        betweenGroups.F = fValue;
        betweenGroups.P = pValue;

        withinGroups.SS = ssWithinGroups;
        withinGroups.df = dfWithinGroups;
        withinGroups.MS = msWithinGroups;

        total.df = dfTotal;
        total.SS = ssTotal;

        return new ANOVAStat(betweenGroups, withinGroups, total);
    }
}
