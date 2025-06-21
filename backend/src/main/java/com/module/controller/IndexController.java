package com.module.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 前端路由控制器
 * 处理前端单页应用的路由
 */
@Controller
public class IndexController {

    /**
     * 处理前端路由，返回index.html
     * 确保前端路由能够正常工作
     */
    @RequestMapping(value = {"/", "/manual", "/support", "/feedback"})
    public String index() {
        return "forward:/index.html";
    }
} 