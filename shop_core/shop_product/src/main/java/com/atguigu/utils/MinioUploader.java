package com.atguigu.utils;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@EnableConfigurationProperties(MinioProperties.class)
@Configuration
public class MinioUploader {
    @Autowired
    private MinioProperties minioProperties;
    @Autowired
    private MinioClient minioClient;

    //项目启动的时候会去加载该bean
    @Bean
    public MinioClient minioClient() throws Exception {
        //使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
        MinioClient minioClient = new MinioClient(minioProperties.getEndpoint(), minioProperties.getAccessKey(), minioProperties.getSecretKey());
        //检查存储桶是否已经存在
        boolean isExist = minioClient.bucketExists(minioProperties.getBucketName());
        if (isExist) {
            System.out.println("Bucket already exists.");
        } else {
            //创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
            minioClient.makeBucket(minioProperties.getBucketName());
        }
        return minioClient;
    }

    public String uploadFile(MultipartFile file) throws Exception {
        String fileName = UUID.randomUUID().toString() + file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();
        //使用putObject上传一个文件到存储桶中。
        PutObjectOptions options = new PutObjectOptions(inputStream.available(), -1);
        options.setContentType(file.getContentType());
        minioClient.putObject(minioProperties.getBucketName(), fileName, inputStream, options);
        System.out.println("上传成功");
        //http://192.168.17.128:9000/javatest/new.jpg
        String retUrl = minioProperties.getEndpoint() + "/" + minioProperties.getBucketName() + "/" + fileName;
        return retUrl;
    }
}