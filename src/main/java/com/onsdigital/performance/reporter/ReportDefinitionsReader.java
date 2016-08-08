package com.onsdigital.performance.reporter;

import com.google.gson.Gson;
import com.onsdigital.performance.reporter.model.ReportDefinition;
import com.onsdigital.performance.reporter.model.ReportDefinitions;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ReportDefinitionsReader {

    public ReportDefinitions readReportDefinitions(String filename) throws FileNotFoundException {

        String file = getClass().getClassLoader().getResource(filename).getFile();
        FileReader fileReader = new FileReader(file);
        ReportDefinitions reportDefinitions = new Gson().fromJson(fileReader, ReportDefinitions.class);

        for (ReportDefinition report : reportDefinitions.reports) {
            System.out.println("report.name = " + report.name);
        }

        return reportDefinitions;
    }

}
