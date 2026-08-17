package com.acme.pmms.patterns;

public class SalesReportStrategy implements ReportStrategy {
    @Override
    public void generateReport(String data) {
        System.out.println("\nSALES MANAGEMENT REPORT");
        System.out.println(data);
    }
}