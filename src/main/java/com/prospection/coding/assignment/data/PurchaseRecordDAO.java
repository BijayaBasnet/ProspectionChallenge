package com.prospection.coding.assignment.data;

import com.prospection.coding.assignment.domain.PurchaseRecord;

import java.util.List;

public interface PurchaseRecordDAO {

    List<PurchaseRecord> allPurchaseRecords() throws Exception;

}
