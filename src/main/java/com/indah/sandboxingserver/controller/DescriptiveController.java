package com.indah.sandboxingserver.controller;

import com.indah.sandboxingserver.config.ServerResponse;
import com.indah.sandboxingserver.request.ColumnRequest;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.indah.sandboxingserver.service.DescriptiveService;

@RestController
@RequestMapping("/api/v1/desc")
@CrossOrigin(origins = "*")
public class DescriptiveController {

    @Autowired
    private DescriptiveService descriptiveService;

    @PostMapping("/summary")
    ServerResponse getSummary(@RequestBody ColumnRequest request) {
        var tableName = request.getTableName();
        var columnNames = request.getColumnNames();

        var summary = descriptiveService.getSummary(tableName, columnNames);

        return new ServerResponse(summary.toJSON().collectAsList());
    }
}
