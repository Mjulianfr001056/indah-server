package com.indah.sandboxingserver.db;

import org.apache.spark.sql.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DBManager {
    private static DBManager instance;

    @Autowired
    private SparkSession sparkSession;

    @Value("${indah.data.source.url}")
    private String DB_URL;

    private DBManager() {

    }

    public static DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    public Dataset<Row> getTable(String tableName) {
        return sparkSession.read()
                .format("jdbc")
                .option("url", DB_URL)
                .option("dbtable", tableName)
                .load();
    }

    public Dataset<Row> getTable(String tableName, List<String> columnNames) {
        List<Column> selectedColumns = columnNames.stream()
                .map(functions::col)
                .collect(Collectors.toList());

        return sparkSession.read()
                .format("jdbc")
                .option("url", DB_URL)
                .option("dbtable", tableName)
                .load()
                .select(selectedColumns.toArray(new Column[0]));
    }
}
