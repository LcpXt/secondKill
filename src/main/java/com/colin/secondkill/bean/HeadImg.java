package com.colin.secondkill.bean;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * 2024年06月06日18:11
 */

@Data
@NoArgsConstructor
public class HeadImg {
    private Integer id;
    private User user;
    private Timestamp uploadTime;
    private String originalPath;
    private String mappingPath;
    private String imgType;
    private String imgSize;
    private String originalName;

    private HeadImg(HeadImgBuilder headImgBuilder) {
        this.id = headImgBuilder.id;
        this.user = headImgBuilder.user;
        this.uploadTime = headImgBuilder.uploadTime;
        this.originalPath = headImgBuilder.originalPath;
        this.mappingPath = headImgBuilder.mappingPath;
        this.imgType = headImgBuilder.imgType;
        this.imgSize = headImgBuilder.imgSize;
        this.originalName = headImgBuilder.originalName;
    }

    public static class HeadImgBuilder {
        private Integer id;
        private User user;
        private Timestamp uploadTime;
        private String originalPath;
        private String mappingPath;
        private String imgType;
        private String imgSize;
        private String originalName;

        public HeadImgBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public HeadImgBuilder user(User user) {
            this.user = user;
            return this;
        }

        public HeadImgBuilder uploadTime(Timestamp uploadTime) {
            this.uploadTime = uploadTime;
            return this;
        }

        public HeadImgBuilder originalPath(String originalPath) {
            this.originalPath = originalPath;
            return this;
        }

        public HeadImgBuilder mappingPath(String mappingPath) {
            this.mappingPath = mappingPath;
            return this;
        }

        public HeadImgBuilder imgType(String imgType) {
            this.imgType = imgType;
            return this;
        }

        public HeadImgBuilder imgSize(String imgSize) {
            this.imgSize = imgSize;
            return this;
        }

        public HeadImgBuilder originalName(String originalName) {
            this.originalName = originalName;
            return this;
        }

        public HeadImg build() {
            return new HeadImg(this);
        }
    }
}