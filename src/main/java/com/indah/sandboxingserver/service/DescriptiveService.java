package com.indah.sandboxingserver.service;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.List;

public interface DescriptiveService {
    Dataset<Row> getSummary(String tableId, List<String> columnName);
    String getCorrelation(String tableId, List<String> columnName);

}
