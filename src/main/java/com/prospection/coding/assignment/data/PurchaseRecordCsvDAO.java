package com.prospection.coding.assignment.data;

import com.prospection.coding.assignment.domain.PurchaseRecord;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class PurchaseRecordCsvDAO implements PurchaseRecordDAO {

    @Value("${mock.data.path}")
    private String mockDataPath;

    @Override
    public List<PurchaseRecord> allPurchaseRecords() throws Exception {

        List<PurchaseRecord> records = new ArrayList<>();

        for (Object line : FileUtils.readLines(new File(mockDataPath))) {
            final String[] fields = String.valueOf(line).split(",");
            final int day = Integer.parseInt(fields[0]);
            final String medication = fields[1];
            final int patientId = Integer.parseInt(fields[2]);

            records.add(new PurchaseRecord(day, medication, patientId));
        }

        return records;
    }
}
