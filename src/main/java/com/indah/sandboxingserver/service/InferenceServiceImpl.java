package com.indah.sandboxingserver.service;

import com.indah.sandboxingserver.db.DBManager;
import com.indah.sandboxingserver.entity.ANOVAUtil;
import com.indah.sandboxingserver.entity.TTestStat;
import com.indah.sandboxingserver.request.TTestRequest;
import lombok.var;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.indah.sandboxingserver.mapper.DatasetMapper.mapToDoubleArray;

@Service
public class InferenceServiceImpl implements InferenceService {

    @Autowired
    private DBManager dbManager;

    @Override
    public ANOVAUtil.ANOVAStat getANOVA(String tableId, List<String> columnName) {
        var tableName = dbManager.getInDBTableNameFromId(tableId);
        var table = dbManager.getTable(tableName, columnName);

        ANOVAUtil anovaUtil = new ANOVAUtil();

        for (String column : table.columns()) {
            double[] columnData = mapToDoubleArray(table, column);
            anovaUtil.addData(columnData);
        }

        return anovaUtil.calculate();
    }

    @Override
    public TTestStat tTest(TTestRequest request) {
        var tableName = dbManager.getInDBTableNameFromId(request.getTableId());
        var table = dbManager.getTable(tableName, Collections.singletonList(request.getColumnNames()));

        double[] columnData = mapToDoubleArray(table, request.getColumnNames());
        double mu = request.getMu();
        double df = columnData.length - 1;
        TTestRequest.TTestAlternative alternative = request.getAlternative();

        TDistribution tDistribution = new TDistribution(df);

        SummaryStatistics summaryStatistics = new SummaryStatistics();
        Arrays.stream(columnData).forEach(summaryStatistics::addValue);

        double tStat = TestUtils.t(mu, columnData);
        double pValue = 0.0f;
        double sd = summaryStatistics.getStandardDeviation();
        double sampleMean = summaryStatistics.getMean();
        double ci95Low = 0.0f;
        double ci95High = 0.0f;
        double marginOfError;

        switch (alternative) {
            case LESS:
                pValue = tDistribution.cumulativeProbability(tStat);
                marginOfError = tDistribution.inverseCumulativeProbability(0.95) * (sd / Math.sqrt(columnData.length));
                ci95Low = Double.NEGATIVE_INFINITY;
                ci95High = sampleMean + marginOfError;
                break;
            case GREATER:
                pValue = tDistribution.cumulativeProbability(-tStat);
                marginOfError = tDistribution.inverseCumulativeProbability(0.95) * (sd / Math.sqrt(columnData.length));
                ci95Low = sampleMean - marginOfError;
                ci95High = Double.POSITIVE_INFINITY;
                break;
            case TWO_SIDED:
                pValue = TestUtils.tTest(mu, columnData);
                marginOfError = tDistribution.inverseCumulativeProbability(0.975) * (sd / Math.sqrt(columnData.length));
                ci95Low = sampleMean - marginOfError;
                ci95High = sampleMean + marginOfError;
                break;
        }

        return TTestStat.builder()
                .t(tStat)
                .p(pValue)
                .sd(sd)
                .df(df)
                .mean(sampleMean)
                .ci95Low(ci95Low)
                .ci95High(ci95High)
                .build();
    }

}
