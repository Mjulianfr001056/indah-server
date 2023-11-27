package com.indah.sandboxingserver.entity;

import lombok.Data;
import lombok.var;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.Arrays;
import java.util.List;

@Data
public class SummaryEntity {
    List<String> columnName;
    Object count;
//    List<Row> mean;
//    List<Double> stdev;
//    List<Double> min;
//    List<Double> percentile25;
//    List<Double> percentile50;
//    List<Double> percentile75;
//    List<Double> max;

    public SummaryEntity(Dataset<Row> summary){
        var summaryList = summary.collectAsList();

        columnName = Arrays.asList(summary.columns());
        count = summaryList.get(0);
//        mean = summaryList.get(1).getList(1);
//        stdev = summaryList.get(2).getList(1);
//        min = summaryList.get(3).getList(1);
//        percentile25 = summaryList.get(4).getList(1);
//        percentile50 = summaryList.get(5).getList(1);
//        percentile75 = summaryList.get(6).getList(1);
//        max = summaryList.get(7).getList(1);
    }
}
