package com.acme.pmms.services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {
    public static synchronized void writeReportToFile(String filename, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(content);
            writer.newLine();
            System.out.println("[File I/O] Successfully logged report data to file: " + filename);
        } catch (IOException e) {
            System.err.println("[File I/O Error] Could not write report: " + e.getMessage());
        }
    }
}