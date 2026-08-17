package com.acme.pmms.patterns;

public class ProductionReportStrategy implements ReportStrategy {
    @Override
    public void generateReport(String data) {
        System.out.println("\nPRODUCTION MANAGEMENT REPORT");
        System.out.println(data);
    }
}