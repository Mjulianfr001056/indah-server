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

        return sparkSession.read()
                .format("jdbc")
                .option("url", DB_URL)
                .option("dbtable", tableName)
                .load()
                .select(columnNames.stream().map(functions::col).toArray(Column[]::new));
    }

    public Dataset<Row> getJoinedTable(Dataset<Row> dataset, String tableName, String joinColumn) {
        return dataset.join(getTable(tableName), joinColumn);
    }

    public String getInDBTableNameFromId(String tableId){
        Dataset<Row> index = sparkSession.read()
                .format("jdbc")
                .option("url", DB_URL)
                .option("dbtable", "data_indexer")
                .load();

        for (Row row : index.collectAsList()) {
            if (row.getString(2).equals(tableId)) {
                return row.getString(1);
            }
        }

        return null;
    }
}
