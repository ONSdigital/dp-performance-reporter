package com.onsdigital.performance.reporter.interfaces;

import com.onsdigital.performance.reporter.model.Metrics;

public interface FileUploader {

    /**
     * Take some metrics, serialise to JSON, and upload
     *
     * @param metrics
     * @param name
     */
    void uploadJsonForObject(Metrics metrics, String name);

}
