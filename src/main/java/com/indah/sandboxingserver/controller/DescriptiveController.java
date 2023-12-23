package com.indah.sandboxingserver.controller;

import com.indah.sandboxingserver.config.ServerResponse;
import com.indah.sandboxingserver.request.DescriptiveRequest;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.indah.sandboxingserver.service.DescriptiveService;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
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

    @PostMapping()
    ServerResponse getDescriptiveResponse(@RequestBody DescriptiveRequest request) {
        var tableId = request.getTableId();
        var columnNames = request.getColumnNames();
        var descriptiveMethods = request.getDescriptiveMethods();

        Map<String, Object> response = new HashMap<>();

        for (var method : descriptiveMethods){
            switch (method) {
                case "Summary":
                    var summary = descriptiveService.getSummary(tableId, columnNames);
                    response.put("Summary", summary.toJSON().collectAsList().toString());
                    break;
                case "Correlation":
                    var correlation = descriptiveService.getCorrelation(tableId, columnNames);
                    response.put("Correlation", correlation);
                    break;
                default:
                    break;
            }
        }

        return new ServerResponse(response);
    }
}
