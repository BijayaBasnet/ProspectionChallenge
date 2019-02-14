package com.prospection.coding.assignment.domain;

public class PurchaseRecord {

    private int day;
    private String medication;
    private int patientId;

    public PurchaseRecord(int day, String medication, int patientId) {
        this.day = day;
        this.medication = medication;
        this.patientId = patientId;
    }

    public int getDay() {
        return day;
    }

    public PurchaseRecord setDay(int day) {
        this.day = day;
        return this;
    }

    public String getMedication() {
        return medication;
    }

    public PurchaseRecord setMedication(String medication) {
        this.medication = medication;
        return this;
    }

    public int getPatientId() {
        return patientId;
    }

    public PurchaseRecord setPatientId(int patientId) {
        this.patientId = patientId;
        return this;
    }

    @Override
    public String toString() {
        return day + "," + medication + "," + patientId;
    }
}