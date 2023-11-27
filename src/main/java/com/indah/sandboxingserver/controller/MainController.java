package com.indah.sandboxingserver.controller;


import com.indah.sandboxingserver.config.ServerResponse;
import com.indah.sandboxingserver.db.DBManager;
import com.indah.sandboxingserver.request.ColumnRequest;
import com.indah.sandboxingserver.service.DescriptiveService;
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

    @GetMapping("/{tableName}")
    public ServerResponse getTable(@RequestParam("tableName") String tableName) {
        var tabel = dbManager.getTable(tableName);
        return new ServerResponse(tabel.toJSON().collectAsList());
    }

    @PostMapping()
    public ServerResponse getTable(@RequestBody ColumnRequest request) {
        var tableName = request.getTableName();
        var columnNames = request.getColumnNames();

        var response = dbManager.getTable(tableName, columnNames);

        return new ServerResponse(response.toJSON().collectAsList());
    }
}
