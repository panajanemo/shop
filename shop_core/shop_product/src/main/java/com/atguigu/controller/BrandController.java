package com.atguigu.controller;


import com.atguigu.entity.BaseBrand;
import com.atguigu.result.RetVal;
import com.atguigu.service.BaseBrandService;
import com.atguigu.utils.MinioUploader;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 品牌表 前端控制器
 * </p>
 *
 * @author zhangqiang
 * @since 2022-08-26
 */
@RestController
@RequestMapping("/product/brand")
public class BrandController {

    @Value("${fastdfs.prefix}")
    public String fastdfsPrefix;

    @Autowired
    private BaseBrandService brandService;

    @Autowired
    private MinioUploader minioUploader;

    /**
     * @param currentPageNum
     * @param pageSize
     * @return RetVal
     * @description 分页查询品牌列表
     * @author panajanemo
     * @time 2023/2/16 1:45
     */
    @GetMapping("queryBrandByPage/{currentPageNum}/{pageSize}")
    public RetVal queryBrandByPage(@PathVariable Long currentPageNum, @PathVariable Long pageSize) {
        IPage<BaseBrand> page = new Page<>(currentPageNum, pageSize);
        brandService.page(page, null);
        return RetVal.ok(page);
    }

    /**
     * @param brand
     * @return RetVal
     * @description 添加品牌
     * @author panajanemo
     * @time 2023/2/16 1:45
     */
    @PostMapping
    public RetVal saveBrand(@RequestBody BaseBrand brand) {
        brandService.save(brand);
        return RetVal.ok();
    }

    /**
     * @param brandId
     * @return RetVal
     * @description 根据id查询品牌信息
     * @author panajanemo
     * @time 2023/2/16 1:45
     */
    @GetMapping("{brandId}")
    public RetVal saveBrand(@PathVariable Long brandId) {
        BaseBrand brand = brandService.getById(brandId);
        return RetVal.ok(brand);
    }

    /**
     * @param brand
     * @return RetVal
     * @description 更新品牌信息
     * @author panajanemo
     * @time 2023/2/16 1:44
     */
    @PutMapping
    public RetVal updateBrand(@RequestBody BaseBrand brand) {
        brandService.updateById(brand);
        return RetVal.ok();
    }

    /**
     * @param brandId
     * @return RetVal
     * @description 删除品牌信息
     * @author panajanemo
     * @time 2023/2/16 1:43
     */
    @DeleteMapping("{brandId}")
    public RetVal remove(@PathVariable Long brandId) {
        brandService.removeById(brandId);
        return RetVal.ok();
    }

    /**
     * @return RetVal
     * @description 查询所有的品牌
     * @author panajanemo
     * @time 2023/2/16 1:43
     */
    @GetMapping("getAllBrand")
    public RetVal getAllBrand() {
        List<BaseBrand> brandList = brandService.list(null);
        return RetVal.ok(brandList);
    }

    ///**
    // * @description 图片上传 http://api.gmall.com/product/brand/fileUpload
    // * @param file
    // * @return RetVal
    // * @author panajanemo
    // * @time 2023/2/16 22:09
    // */
    //@PostMapping("fileUpload")
    //public RetVal fileUpload(MultipartFile file) throws Exception{
    //    //需要一个配置文件告诉fastdfs在哪里
    //    String configFilePath = this.getClass().getResource("/tracker.conf").getFile();
    //    //初始化
    //    ClientGlobal.init(configFilePath);
    //    //创建trackerClient 客户端
    //    TrackerClient trackerClient = new TrackerClient();
    //    //用trackerClient获取连接
    //    TrackerServer trackerServer = trackerClient.getConnection();
    //    //创建StorageClient1
    //    StorageClient1 storageClient1 = new StorageClient1(trackerServer, null);
    //    //对文件实现上传
    //    String originalFilename = file.getOriginalFilename();
    //    String extension = FilenameUtils.getExtension(originalFilename);
    //    String path = storageClient1.upload_appender_file1(file.getBytes(), extension, null);
    //    System.out.println("文件上传地址:"+fastdfsPrefix+path);
    //    return RetVal.ok(fastdfsPrefix+path);
    //}

    /**
     * @param file
     * @return RetVal
     * @description 图片上传
     * @author panajanemo
     * @time 2023/2/20 12:08
     */

    @PostMapping("fileUpload")
    public RetVal fileUplpad(MultipartFile file) throws Exception {
        String retUrl = minioUploader.uploadFile(file);
        return RetVal.ok(retUrl);
    }
}

