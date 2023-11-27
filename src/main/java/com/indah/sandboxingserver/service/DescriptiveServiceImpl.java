package com.indah.sandboxingserver.service;

import com.indah.sandboxingserver.config.ServerResponse;
import com.indah.sandboxingserver.db.DBManager;
import lombok.var;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DescriptiveServiceImpl implements DescriptiveService {
    @Autowired
    DBManager dbManager;

    @Override
    public Dataset<Row> getSummary(String tableName, List<String> columnName) {
        var table = dbManager.getTable(tableName, columnName);

        return table.summary();
    }
}
