package com.atguigu;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.errors.MinioException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class FileUploader {
  public static void main(String[] args) throws NoSuchAlgorithmException, IOException, InvalidKeyException, XmlPullParserException {
    try {
      //使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
      MinioClient minioClient = new MinioClient("http://192.168.17.128:9000", "admin", "12345678");
      //检查存储桶是否已经存在
      boolean isExist = minioClient.bucketExists("javatest");
      if(isExist) {
        System.out.println("Bucket already exists.");
      } else {
        //创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
        minioClient.makeBucket("javatest");
      }
      //使用putObject上传一个文件到存储桶中。地址：http://192.168.17.128:9000/javatest/1.jpg.
      FileInputStream fileInputStream = new FileInputStream("D:\\Java\\temp\\1.jpg");
      PutObjectOptions options=new PutObjectOptions(fileInputStream.available(),-1);
      options.setContentType("image/jpeg");
      minioClient.putObject("javatest","1.jpg",fileInputStream, options);
      System.out.println("上传成功");
    } catch(MinioException e) {
      System.out.println("Error occurred: " + e);
    }
  }
}