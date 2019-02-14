package com.prospection.coding.assignment.domain;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class AnalysisResult {

    private Map<PatientType, Integer> patients;

    private Map<PatientType, Map<Integer, List<PurchaseRecord>>> categorizedPurchaseRecords;

    public AnalysisResult() {
        patients = new EnumMap<>(PatientType.class);
        categorizedPurchaseRecords = new EnumMap<>(PatientType.class);
    }

    public void putTotal(PatientType patientType, int total, Map<Integer, List<PurchaseRecord>> categorizedPurchaseRecords) {
        this.patients.put(patientType, total);
        this.categorizedPurchaseRecords.put(patientType, categorizedPurchaseRecords);
    }

    public Integer getTotal(PatientType patientType) {
        return this.patients.get(patientType);
    }

    public Map<PatientType, Integer> getPatients() {
        return patients;
    }

    public AnalysisResult setPatients(Map<PatientType, Integer> patients) {
        this.patients = patients;
        return this;
    }


    public Map<PatientType, Map<Integer, List<PurchaseRecord>>> getCategorizedPurchaseRecords() {
        return categorizedPurchaseRecords;
    }

    public void setCategorizedPurchaseRecords(Map<PatientType, Map<Integer, List<PurchaseRecord>>> categorizedPurchaseRecords) {
        this.categorizedPurchaseRecords = categorizedPurchaseRecords;
    }

    public Map<PatientType, String> getPatientTypeNameMap() {
        return Arrays.stream(PatientType.values())
                .collect(toMap(identity(), PatientType::getName));
    }

    public enum PatientType {

        VIOLATED("Patients that violated by taking B and I together."), //
        VALID_NO_COMED("Patients that did not violate, because they never took B and I together."), //
        VALID_BI_SWITCH("Patients that did not violate, because they switched from B to I."), //
        VALID_IB_SWITCH("Patients that did not violate, because they switched from I to B."), //
        VALID_I_TRIAL("Patients that did not violate, because they simply trialled I during B."), //
        VALID_B_TRIAL("Patients that did not violate, because they simply trialled B during I.");

        private String name;

        PatientType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}