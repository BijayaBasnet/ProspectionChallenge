package com.prospection.coding.assignment.service;

import com.prospection.coding.assignment.data.PurchaseRecordDAO;
import com.prospection.coding.assignment.domain.AnalysisResult;
import com.prospection.coding.assignment.domain.PurchaseRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static com.prospection.coding.assignment.domain.AnalysisResult.PatientType.VALID_BI_SWITCH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BIAnalysisServiceTest {

    @Mock
    private PurchaseRecordDAO purchaseRecordDAO;

    private BIAnalysisService biAnalysisService;

    @Before
    public void setUp() {
        biAnalysisService = new BIAnalysisService(purchaseRecordDAO);
    }

    @Test
    public void shouldProbablyDoSomeTestingHereIsABrokenExample() throws Exception {

        setupPatients(
                new PurchaseRecord(1, "B", 1),
                new PurchaseRecord(100, "I", 1)
        );

        AnalysisResult result = biAnalysisService.performBIAnalysis();

        assertThat("Valid B to I switches should be 1.", result.getTotal(VALID_BI_SWITCH), is(1));

    }

    private void setupPatients(PurchaseRecord... values) throws Exception {
        when(purchaseRecordDAO.allPurchaseRecords())
                .thenReturn(Arrays.asList(values));
    }
}