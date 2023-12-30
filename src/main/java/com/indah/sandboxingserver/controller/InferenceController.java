package com.indah.sandboxingserver.controller;

import com.indah.sandboxingserver.config.ServerResponse;
import com.indah.sandboxingserver.entity.ANOVAUtil;
import com.indah.sandboxingserver.request.ANOVARequest;
import com.indah.sandboxingserver.request.ColumnRequest;
import com.indah.sandboxingserver.request.TTestRequest;
import lombok.var;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.indah.sandboxingserver.service.InferenceService;

@RestController
@RequestMapping("/api/v1/inference")
@CrossOrigin(origins = "*")
public class InferenceController {
    @Autowired
    private InferenceService inferenceService;

    @PostMapping("/anova")
    ServerResponse getANOVA(@RequestBody ANOVARequest request) {
        var tableId = request.getTableId();
        var columnNames = request.getColumnNames();

        ANOVAUtil.ANOVAStat anova = inferenceService.getANOVA(tableId, columnNames);

        return new ServerResponse(anova.toJSON());
    }

    @PostMapping("/ttest")
    ServerResponse getTTest(@RequestBody TTestRequest request) {
        var response = inferenceService.tTest(request);

        return new ServerResponse(response);
    }

    @PostMapping("/paired-ttest")
    ServerResponse getPairedTTest(@RequestBody ColumnRequest request) {
        var tableId = request.getTableId();
        var columnNames = request.getColumnNames();
        var response = inferenceService.pairedTTest(tableId, columnNames.get(0), columnNames.get(1));

        return new ServerResponse(response);
    }

    @PostMapping("/wilcoxon")
    ServerResponse getWilcoxon(@RequestBody ColumnRequest request) {
        var tableId = request.getTableId();
        var columnNames = request.getColumnNames();
        var response = inferenceService.wilcoxonTest(tableId, columnNames.get(0), columnNames.get(1));

        return new ServerResponse(response);
    }

    @PostMapping("/mann-whitney")
    ServerResponse getMannWhitney(@RequestBody ColumnRequest request) {
        var tableId = request.getTableId();
        var columnNames = request.getColumnNames();
        var response = inferenceService.mannWhitneyTest(tableId, columnNames.get(0), columnNames.get(1));

        return new ServerResponse(response);
    }
}
