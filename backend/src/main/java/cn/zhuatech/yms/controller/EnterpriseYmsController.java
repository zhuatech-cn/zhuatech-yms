/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.yms.controller;
import cn.zhuatech.yms.common.ApiResponse; import cn.zhuatech.yms.service.EnterpriseYmsService; import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/enterprise/yms") public class EnterpriseYmsController {
 private final EnterpriseYmsService service; public EnterpriseYmsController(EnterpriseYmsService service){this.service=service;}
 @PostMapping("/evaluate-slot") ApiResponse<?> execute(@Valid @RequestBody EnterpriseYmsService.SlotRequest request){return ApiResponse.ok(service.evaluate(request));}
}

