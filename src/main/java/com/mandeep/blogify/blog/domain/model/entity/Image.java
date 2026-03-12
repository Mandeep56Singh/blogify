package com.mandeep.blogify.blog.domain.model.entity;


import com.mandeep.blogify.blog.domain.model.valueObject.ImageId;
import com.mandeep.blogify.blog.domain.model.valueObject.ImageMetadata;
import lombok.Getter;

import java.time.Instant;

@Getter
public class Image {
    private final ImageId id;
    private final ImageMetadata metadata;
    private final String s3Key; // The unique path in your AWS Bucket
    private final Instant uploadedAt;

    private Image(ImageId id, ImageMetadata metadata, String s3Key, Instant uploadedAt) {
        this.id = id;
        this.metadata = metadata;
        this.s3Key = s3Key;
        this.uploadedAt = uploadedAt;
    }

    public static Image upload(ImageId id, ImageMetadata metadata, String s3Key) {
        return new Image(id, metadata, s3Key, Instant.now());
    }

    public static Image reconstitute(ImageId id, ImageMetadata metadata, String s3Key, Instant uploadedAt) {
        return new Image(id, metadata, s3Key, uploadedAt);
    }

}