package com.indah.sandboxingserver.service;

import com.indah.sandboxingserver.entity.ANOVAUtil;

import java.util.List;

public interface InferenceService {
    ANOVAUtil.ANOVAStat getANOVA(String tableId, List<String> columnName);

}
