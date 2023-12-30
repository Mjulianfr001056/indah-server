package com.indah.sandboxingserver.controller;

import com.indah.sandboxingserver.config.ServerResponse;
import com.indah.sandboxingserver.request.DescriptiveRequest;
import com.indah.sandboxingserver.service.DescriptiveService;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/desc")
@CrossOrigin(origins = "*")
public class DescriptiveController {

    @Autowired
    private DescriptiveService descriptiveService;

    @PostMapping("/summary")
    ServerResponse getSummary(@RequestBody DescriptiveRequest request) {
        var tableId = request.getTableId();
        var columnNames = request.getColumnNames();

        var summary = descriptiveService.getSummary(tableId, columnNames);

        return new ServerResponse(summary.toJSON().collectAsList());
    }

    @PostMapping("/correlation")
    ServerResponse getCorrelation(@RequestBody DescriptiveRequest request) {
        var tableId = request.getTableId();
        var columnNames = request.getColumnNames();

        var correlation = descriptiveService.getCorrelation(tableId, columnNames);

        return new ServerResponse(correlation);
    }
}
