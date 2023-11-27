package com.indah.sandboxingserver.service;

import com.indah.sandboxingserver.config.ServerResponse;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.List;

public interface DescriptiveService {
    Dataset<Row> getSummary(String tableName, List<String> columnName);

}
