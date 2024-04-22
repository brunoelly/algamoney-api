package com.algaworks.algamoneyapi.storage;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import com.algaworks.algamoneyapi.config.property.AlgamoneyApiProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Tag;

@Component
public class S3 {

    private static final Logger logger = LoggerFactory.getLogger(S3.class);

    @Autowired
    private AlgamoneyApiProperty property;

    @Autowired
    private AmazonS3 amazonS3;

    public String temporarySave(MultipartFile file) {
        AccessControlList acl = new AccessControlList();
        acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        String uniqueName = generateUniqueName(file.getOriginalFilename());

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    property.getS3().getBucket(),
                    uniqueName,
                    file.getInputStream(),
                    objectMetadata)
                    .withAccessControlList(acl);

            putObjectRequest.setTagging(new ObjectTagging(
                    Arrays.asList(new Tag("expire", "true"))));

            amazonS3.putObject(putObjectRequest);

            if (logger.isDebugEnabled()) {
                logger.debug("File {} sent successfully onto S3.",
                        file.getOriginalFilename());
            }

            return uniqueName;
        } catch (IOException e) {
            throw new RuntimeException("Error when saving file onto S3.", e);
        }
    }

    public String configureUrl(String object) {
        return "\\\\" + property.getS3().getBucket() +
                ".s3.amazonaws.com/" + object;
    }

    public void remove(String object) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(
                property.getS3().getBucket(), object);

        amazonS3.deleteObject(deleteObjectRequest);
    }

    public void substitute(String formerObject, String newObject) {
        if (StringUtils.hasText(formerObject)) {
            this.remove(formerObject);
        }

        save(newObject);
    }

    public void save(String object) {
        SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(
                property.getS3().getBucket(),
                object,
                new ObjectTagging(Collections.emptyList()));

        amazonS3.setObjectTagging(setObjectTaggingRequest);
    }

    private String generateUniqueName(String originalFilename) {
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }
}