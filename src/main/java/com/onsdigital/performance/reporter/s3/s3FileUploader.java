package com.onsdigital.performance.reporter.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.google.gson.Gson;
import com.onsdigital.performance.reporter.Configuration;
import com.onsdigital.performance.reporter.interfaces.FileUploader;
import com.onsdigital.performance.reporter.model.Metrics;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class s3FileUploader implements FileUploader {

    private static Log log = LogFactory.getLog(s3FileUploader.class);
    private static String bucketName = Configuration.getAwsBucketName();

    private TransferManager transferManager = new TransferManager();
    private Gson gson = new Gson();

    /**
     * Take an object, serialise to JSON, and upload
     *
     * @param metrics
     * @param name
     */
    public void uploadJsonForObject(Metrics metrics, String name) {

        log.debug("Uploading " + name + " to S3...");
        String json = gson.toJson(metrics);

        byte[] bytes = json.getBytes();
        InputStream input = new ByteArrayInputStream(bytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        metadata.setContentDisposition("inline");
        transferManager.upload(
                new PutObjectRequest(
                        bucketName, name, input, metadata)
        );
        log.debug("Upload of " + name + " is complete.");
    }

    public static void main(String[] args) throws IOException {

        String bucketName = "ons-metrics";
        String keyName = "test";
        String uploadFileName = "test.json";

        AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());

        try {
            System.out.println("Uploading a new object to S3 from a file\n");
            File file = new File(uploadFileName);
            s3client.putObject(
                    new PutObjectRequest(
                            bucketName, keyName, file));

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " +
                    "means your request made it " +
                    "to Amazon S3, but was rejected with an error response" +
                    " for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " +
                    "means the client encountered " +
                    "an internal error while trying to " +
                    "communicate with S3, " +
                    "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
}
