package com.indah.sandboxingserver.service;

import com.indah.sandboxingserver.entity.ANOVAUtil;
import com.indah.sandboxingserver.entity.TTestStat;
import com.indah.sandboxingserver.request.TTestRequest;

import java.util.List;

public interface InferenceService {
    ANOVAUtil.ANOVAStat getANOVA(String tableId, List<String> columnName);

    TTestStat tTest(TTestRequest request);

    TTestStat pairedTTest(String tableId, String columnName1, String columnName2);
}
