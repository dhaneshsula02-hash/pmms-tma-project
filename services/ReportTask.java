package com.acme.pmms.services;

import com.acme.pmms.patterns.ReportStrategy;

public class ReportTask implements Runnable {
    private ReportStrategy strategy;
    private String reportData;
    private String filename;

    public ReportTask(ReportStrategy strategy, String reportData, String filename) {
        this.strategy = strategy;
        this.reportData = reportData;
        this.filename = filename;
    }

    @Override
    public void run() {
        System.out.println("[Thread - Reporting] Running reporting task...");
        strategy.generateReport(reportData);
        FileManager.writeReportToFile(filename, reportData);
    }
}