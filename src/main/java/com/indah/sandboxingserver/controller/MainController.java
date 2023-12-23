package com.indah.sandboxingserver.controller;


import com.indah.sandboxingserver.config.ServerResponse;
import com.indah.sandboxingserver.db.DBManager;
import com.indah.sandboxingserver.request.ColumnRequest;
import com.indah.sandboxingserver.request.RowRequest;
import com.indah.sandboxingserver.response.GetTableResponse;
import lombok.var;
import org.apache.spark.sql.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/data")
@CrossOrigin(origins = "*")
public class MainController {
    @Autowired
    DBManager dbManager;

    @GetMapping()
    public ServerResponse getTable() {
        var tabel = dbManager.getTable("data_sampel2");
        return new ServerResponse(tabel.toJSON().collectAsList());
    }

    @GetMapping("/{tableId}")
    public ServerResponse getTable(@PathVariable String tableId) {
        String inDBTableName = dbManager.getInDBTableNameFromId(tableId);
        var tabel = dbManager.getTable(inDBTableName);
        String[] tabelHeader = tabel.columns();
        GetTableResponse response = new GetTableResponse(tabelHeader, tabel.toJSON().collectAsList());
        return new ServerResponse(response);
    }


    @PostMapping("/katalog")
    public ServerResponse getKatalog() {
        var tableName = "katalog_data";
        var columnNames = Arrays.asList("id", "judul");

        var response = dbManager.getTable(tableName, columnNames);

        return new ServerResponse(response.toJSON().collectAsList());
    }

    @PostMapping()
    public ServerResponse getTable(@RequestBody ColumnRequest request) {
        var tableId = request.getTableId();

        var tableName = dbManager.getInDBTableNameFromId(tableId);
        var columnNames = request.getColumnNames();

        var response = dbManager.getTable(tableName, columnNames);

        return new ServerResponse(response.toJSON().collectAsList());
    }

    @PostMapping("/row")
    public ServerResponse getTableByRow(@RequestBody RowRequest request) {
        var tableId = request.getTableId();
        var tableName = dbManager.getInDBTableNameFromId(tableId);
        var rowNames = request.getRowNames();

        var rowList = dbManager.getTable(tableName).collectAsList();
        Map<String, String> desiredRows = new HashMap<>();

        for (Row row : rowList) {
            String rowName = row.getString(0);
            if (rowNames.contains(rowName)) {
                String rowString = row.toString();
                int indexOfFirstComma = rowString.indexOf(',');
                String modifiedRowString = "[" + rowString.substring(indexOfFirstComma + 1).trim();
                desiredRows.put(rowName, modifiedRowString);
            }
        }

        return new ServerResponse(desiredRows);
    }

    @PostMapping("/ket")
    public ServerResponse getKeterangan(@RequestBody Map<String, String> request) {
        var tableId = request.get("tableId");
        var tableName = dbManager.getInDBTableNameFromId(tableId);
        tableName= tableName + "_metadata";
        var table = dbManager.getMetadataTable(tableName);
        table.drop("id");


        return new ServerResponse(table.toJSON().collectAsList());
    }
}
