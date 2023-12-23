package com.indah.sandboxingserver.service;

import com.indah.sandboxingserver.db.DBManager;
import com.indah.sandboxingserver.entity.ANOVAUtil;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.indah.sandboxingserver.mapper.DatasetMapper.mapToDoubleArray;

@Service
public class InferenceServiceImpl implements InferenceService {

    @Autowired
    private DBManager dbManager;

    @Override
    public ANOVAUtil.ANOVAStat getANOVA(String tableId, List<String> columnName) {
        var tableName = dbManager.getInDBTableNameFromId(tableId);
        var table = dbManager.getTable(tableName, columnName);

        ANOVAUtil anovaUtil = new ANOVAUtil();

        for (String column : table.columns()) {
            double[] columnData = mapToDoubleArray(table, column);
            anovaUtil.addData(columnData);
        }

        return anovaUtil.calculate();
    }
}
