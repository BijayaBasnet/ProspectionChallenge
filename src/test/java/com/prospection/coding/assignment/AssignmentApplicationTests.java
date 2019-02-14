package com.prospection.coding.assignment;

import com.prospection.coding.assignment.data.PurchaseRecordDAO;
import com.prospection.coding.assignment.service.BIAnalysisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AssignmentApplicationTests {

    @Autowired
    ApplicationContext context;

    @Test
    public void contextLoads() {
        assertThat(context.getBean(BIAnalysisService.class))
                .as("Should have a BI Analysis Service available.")
                .isNotNull();

        assertThat(context.getBean(PurchaseRecordDAO.class))
                .as("Should have a Purchase Record DAO available.")
                .isNotNull();
    }

}
