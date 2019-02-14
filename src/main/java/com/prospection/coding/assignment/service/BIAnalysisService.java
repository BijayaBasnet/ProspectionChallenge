package com.prospection.coding.assignment.service;

import com.prospection.coding.assignment.data.PurchaseRecordDAO;
import com.prospection.coding.assignment.domain.AnalysisResult;
import com.prospection.coding.assignment.domain.PurchaseRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.prospection.coding.assignment.domain.AnalysisResult.PatientType.*;

@Component
public class BIAnalysisService {

    private Map<String, Integer> drugSwitchMinimumDays = new HashMap<String, Integer>() {{
        put("b-min-days", 30);
        put("i-min-days", 90);
    }};

    private Map<String, String> defaultDrugTypes = new HashMap<String, String>() {{
        put("b", "B");
        put("i", "I");
    }};

    private Map<AnalysisResult.PatientType, Integer> possibleCasesWithCount = new HashMap<AnalysisResult.PatientType, Integer>() {{
        put(VIOLATED, 0);
        put(VALID_NO_COMED, 0);
        put(VALID_BI_SWITCH, 0);
        put(VALID_IB_SWITCH, 0);
        put(VALID_I_TRIAL, 0);
        put(VALID_B_TRIAL, 0);
    }};


    private Map<AnalysisResult.PatientType, Map<Integer, List<PurchaseRecord>>> categorizedPurchaseRecords = new HashMap<AnalysisResult.PatientType, Map<Integer, List<PurchaseRecord>>>() {{
        put(VIOLATED, null);
        put(VALID_NO_COMED, null);
        put(VALID_BI_SWITCH, null);
        put(VALID_IB_SWITCH, null);
        put(VALID_I_TRIAL, null);
        put(VALID_B_TRIAL, null);
    }};
    ;

    private String drugBMinimumSwitchDaysKey = "b-min-days";
    private String drugBKey = "b";
    private String drugIMinimumSwitchDaysKey = "i-min-days";
    private String drugIKey = "i";

    private final PurchaseRecordDAO purchaseRecordDAO;

    @Autowired
    public BIAnalysisService(PurchaseRecordDAO purchaseRecordDAO) {
        this.purchaseRecordDAO = purchaseRecordDAO;
    }

    public AnalysisResult performBIAnalysis() throws Exception {
        List<PurchaseRecord> purchaseRecords = purchaseRecordDAO.allPurchaseRecords();
//        List<PurchaseRecord> purchaseRecords = Arrays.asList(new PurchaseRecord(1, "I", 1),
//                new PurchaseRecord(100, "B", 1));
        //Group patient data by their ID

        Map<Integer, List<PurchaseRecord>> groupedPurchasedListByPatientId =
                purchaseRecords.stream().collect(Collectors.groupingBy(PurchaseRecord::getPatientId));


        applyViolationRules(groupedPurchasedListByPatientId);


        // then put real results in here
        AnalysisResult result = new AnalysisResult();
        result.putTotal(VIOLATED, possibleCasesWithCount.get(VIOLATED), categorizedPurchaseRecords.get(VIOLATED));
        result.putTotal(VALID_NO_COMED, possibleCasesWithCount.get(VALID_NO_COMED), categorizedPurchaseRecords.get(VALID_NO_COMED));
        result.putTotal(VALID_BI_SWITCH, possibleCasesWithCount.get(VALID_BI_SWITCH), categorizedPurchaseRecords.get(VALID_BI_SWITCH));
        result.putTotal(VALID_IB_SWITCH, possibleCasesWithCount.get(VALID_IB_SWITCH), categorizedPurchaseRecords.get(VALID_IB_SWITCH));
        result.putTotal(VALID_I_TRIAL, possibleCasesWithCount.get(VALID_I_TRIAL), categorizedPurchaseRecords.get(VALID_I_TRIAL));
        result.putTotal(VALID_B_TRIAL, possibleCasesWithCount.get(VALID_B_TRIAL), categorizedPurchaseRecords.get(VALID_B_TRIAL));

        resetPossibleCasesWithCount();

        return result;
    }

    private void resetPossibleCasesWithCount() {

        possibleCasesWithCount.replace(VALID_I_TRIAL, 0);
        possibleCasesWithCount.replace(VALID_B_TRIAL, 0);
        possibleCasesWithCount.replace(VALID_BI_SWITCH, 0);
        possibleCasesWithCount.replace(VALID_IB_SWITCH, 0);
        possibleCasesWithCount.replace(VIOLATED, 0);
        possibleCasesWithCount.replace(VALID_NO_COMED, 0);
    }


    private void applyViolationRules(Map<Integer, List<PurchaseRecord>> groupedPurchasedListByPatientId) {
        //For each purchase record apply condition
        groupedPurchasedListByPatientId.forEach((key, purchaseRecordsOfAPatient) -> {

            Map<String, Long> counterMap = purchaseRecordsOfAPatient.stream().collect(Collectors.groupingBy(PurchaseRecord::getMedication, Collectors.counting()));

            //Sort purchase records by days
            purchaseRecordsOfAPatient.sort(Comparator.comparing(PurchaseRecord::getDay));

            if (counterMap.size() > 1) {

                //Change map's value to array of PurchaseRecordsObject
                // => For checking last element in an array
                PurchaseRecord[] arraysOfPurchases = purchaseRecordsOfAPatient.toArray(new PurchaseRecord[0]);

                // If both count of B and I are 1 then check the last occurrence of drug and assign it to drug switch
                if (counterMap.get(defaultDrugTypes.get(drugBKey)) == 1 && counterMap.get(defaultDrugTypes.get(drugIKey)) == 1) {

                    if (arraysOfPurchases[arraysOfPurchases.length - 1].getMedication().equalsIgnoreCase(defaultDrugTypes.get(drugBKey))) {

                        incrementAndAddIBSwitch(purchaseRecordsOfAPatient);

                    } else {

                        incrementAndAddBISwitch(purchaseRecordsOfAPatient);

                    }

                } else if (counterMap.get(defaultDrugTypes.get(drugBKey)) == 1 || counterMap.get(defaultDrugTypes.get(drugIKey)) == 1) {

                    if (counterMap.get(defaultDrugTypes.get(drugBKey)) == 1) {

                        if (arraysOfPurchases[arraysOfPurchases.length - 1].getMedication().equalsIgnoreCase(defaultDrugTypes.get(drugBKey))) {

                            incrementAndAddIBSwitch(purchaseRecordsOfAPatient);

                        } else {

                            incrementAndAddValidBTrial(purchaseRecordsOfAPatient);


                        }

                    } else {

                        if (arraysOfPurchases[arraysOfPurchases.length - 1].getMedication().equalsIgnoreCase(defaultDrugTypes.get(drugIKey))) {

                            incrementAndAddBISwitch(purchaseRecordsOfAPatient);


                        } else {

                            incrementAndAddValidITrial(purchaseRecordsOfAPatient);

                        }
                    }

                } else {

                    boolean violated = false;

                    //Check each object with other
                    for (int i = 1; i < purchaseRecordsOfAPatient.size() - 1; i++) {

                        PurchaseRecord purchaseRecord = purchaseRecordsOfAPatient.get(i - 1);
                        PurchaseRecord purchaseRecord1 = purchaseRecordsOfAPatient.get(i);


                        if (!purchaseRecord.getMedication().equals(purchaseRecord1.getMedication())) {

                            if (purchaseRecord.getMedication().equalsIgnoreCase(defaultDrugTypes.get(drugBKey))) {

                                if ((purchaseRecord.getDay() - purchaseRecord1.getDay()) >= drugSwitchMinimumDays.get(drugBMinimumSwitchDaysKey)) {

                                    incrementAndAddBISwitch(purchaseRecordsOfAPatient);

                                    violated = false;

                                } else {

                                    violated = true;

                                }

                            } else if (purchaseRecord.getMedication().equalsIgnoreCase(defaultDrugTypes.get(drugIKey))) {

                                if ((purchaseRecord.getDay() - purchaseRecord1.getDay()) >= drugSwitchMinimumDays.get(drugIMinimumSwitchDaysKey)) {

                                    incrementAndAddIBSwitch(purchaseRecordsOfAPatient);

                                    violated = false;

                                } else {

                                    violated = true;

                                }
                            }
                        }

                    }

                    if (violated) {

                        incrementAndAddViolated(purchaseRecordsOfAPatient);

                    }
                }

            } else {

                incrementAndAddNonViolation(purchaseRecordsOfAPatient);

            }
        });
    }

    private void incrementAndAddValidITrial(List<PurchaseRecord> purchaseRecords) {

        possibleCasesWithCount.replace(VALID_I_TRIAL, possibleCasesWithCount.get(VALID_I_TRIAL) + 1);

        categorizedPurchaseRecords.replace(VALID_I_TRIAL, appendValuesToMap(VALID_I_TRIAL, purchaseRecords));
    }

    private Map<Integer, List<PurchaseRecord>> appendValuesToMap(AnalysisResult.PatientType key, List<PurchaseRecord> purchaseRecords) {
        Map<Integer, List<PurchaseRecord>> tmpMap = new HashMap<Integer, List<PurchaseRecord>>();
        tmpMap = categorizedPurchaseRecords.get(key);
        if (tmpMap != null) {
            tmpMap.putAll(purchaseRecords.stream().collect(Collectors.groupingBy(PurchaseRecord::getPatientId)));
        } else {
            tmpMap = purchaseRecords.stream().collect(Collectors.groupingBy(PurchaseRecord::getPatientId));
        }
        return tmpMap;
    }

    private void incrementAndAddValidBTrial(List<PurchaseRecord> purchaseRecords) {
        possibleCasesWithCount.replace(VALID_B_TRIAL, possibleCasesWithCount.get(VALID_B_TRIAL) + 1);

        categorizedPurchaseRecords.replace(VALID_B_TRIAL, appendValuesToMap(VALID_B_TRIAL, purchaseRecords));
    }

    private void incrementAndAddBISwitch(List<PurchaseRecord> purchaseRecords) {
        possibleCasesWithCount.replace(VALID_BI_SWITCH, possibleCasesWithCount.get(VALID_BI_SWITCH) + 1);

        categorizedPurchaseRecords.replace(VALID_BI_SWITCH, appendValuesToMap(VALID_BI_SWITCH, purchaseRecords));
    }

    private void incrementAndAddIBSwitch(List<PurchaseRecord> purchaseRecords) {
        possibleCasesWithCount.replace(VALID_IB_SWITCH, possibleCasesWithCount.get(VALID_IB_SWITCH) + 1);

        categorizedPurchaseRecords.replace(VALID_IB_SWITCH, appendValuesToMap(VALID_IB_SWITCH, purchaseRecords));

    }

    private void incrementAndAddViolated(List<PurchaseRecord> purchaseRecords) {

        possibleCasesWithCount.replace(VIOLATED, possibleCasesWithCount.get(VIOLATED) + 1);

        categorizedPurchaseRecords.replace(VIOLATED, appendValuesToMap(VIOLATED, purchaseRecords));

    }

    private void incrementAndAddNonViolation(List<PurchaseRecord> purchaseRecords) {
        possibleCasesWithCount.replace(VALID_NO_COMED, possibleCasesWithCount.get(VALID_NO_COMED) + 1);

        categorizedPurchaseRecords.replace(VALID_NO_COMED, appendValuesToMap(VALID_NO_COMED, purchaseRecords));
    }

}
