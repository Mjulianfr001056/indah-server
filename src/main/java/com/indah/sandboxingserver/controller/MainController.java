package com.indah.sandboxingserver.controller;


import com.indah.sandboxingserver.config.ServerResponse;
import com.indah.sandboxingserver.db.DBManager;
import com.indah.sandboxingserver.request.ColumnRequest;
import com.indah.sandboxingserver.response.GetTableResponse;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping()
    public ServerResponse getTable(@RequestBody ColumnRequest request) {
        var tableName = request.getTableName();
        var columnNames = request.getColumnNames();

        var response = dbManager.getTable(tableName, columnNames);

        return new ServerResponse(response.toJSON().collectAsList());
    }
}
