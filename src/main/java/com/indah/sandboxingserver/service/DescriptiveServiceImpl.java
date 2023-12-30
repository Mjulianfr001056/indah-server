package com.indah.sandboxingserver.service;

import com.indah.sandboxingserver.db.DBManager;
import com.indah.sandboxingserver.mapper.DatasetMapper;
import lombok.var;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.stat.Statistics;
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
    public Dataset<Row> getSummary(String tableId, List<String> columnName) {
        var tableName = dbManager.getInDBTableNameFromId(tableId);
        var table = dbManager.getTable(tableName, columnName);

        return table.summary();
    }

    @Override
    public String getCorrelation(String tableId, List<String> columnName) {
        var tableName = dbManager.getInDBTableNameFromId(tableId);
        var table = dbManager.getTable(tableName, columnName);
        var vectorRDD = DatasetMapper.mapToNumericJavaRDDVector(table);

        Matrix correlationMatrix = Statistics.corr(vectorRDD.rdd(), "pearson");

        return correlationMatrix.toString();
    }
}
