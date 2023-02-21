package com.atguigu.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
//在配置文件当中找到以minio开头的配置文件
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}
