package com.onsdigital.performance.reporter;

public interface FileUploader {

    /**
     * Take an object, serialise to JSON, and upload
     *
     * @param object
     * @param name
     */
    void uploadJsonForObject(Object object, String name);

}
