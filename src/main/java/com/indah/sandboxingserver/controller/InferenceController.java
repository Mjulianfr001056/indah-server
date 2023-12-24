package com.indah.sandboxingserver.controller;

import com.indah.sandboxingserver.config.ServerResponse;
import com.indah.sandboxingserver.entity.ANOVAUtil;
import com.indah.sandboxingserver.request.ANOVARequest;
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
}
