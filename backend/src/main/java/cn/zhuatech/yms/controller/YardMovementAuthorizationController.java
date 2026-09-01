/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.yms.controller;

import cn.zhuatech.yms.common.ApiResponse;
import cn.zhuatech.yms.service.YardMovementAuthorizationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enterprise/yms")
public class YardMovementAuthorizationController {
    private final YardMovementAuthorizationService service;
    public YardMovementAuthorizationController(YardMovementAuthorizationService service) { this.service = service; }

    @PostMapping("/yard-movement-authorization")
    public ApiResponse<?> assess(@RequestBody YardMovementAuthorizationService.Request request) {
        return ApiResponse.ok(service.assess(request));
    }
}
