package com.atguigu.controller;


import com.atguigu.service.BaseBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 品牌表 前端控制器
 * </p>
 *
 * @author panajanemo
 * @since 2023-02-25
 */
@RestController
@RequestMapping("/product")
public class ConcurrentController {
    @Autowired
    private BaseBrandService brandService;

    @GetMapping("setNum")
    public String setNum() {
        brandService.setNum();
        return "success";
    }




}

