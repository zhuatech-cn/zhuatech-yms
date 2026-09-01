/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.yms.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class YardMovementAuthorizationService {
    public Result assess(Request request) {
        var blockers = new ArrayList<String>();
        var actions = new ArrayList<String>();
        if (request.visitId() == null || request.visitId().isBlank()) blockers.add("入园访问编号不能为空");
        if (!request.driverVerified()) blockers.add("驾驶员身份未核验");
        if (!request.vehicleQualified()) blockers.add("车辆资质不满足要求");
        if (!request.dockCapacityAvailable()) blockers.add("目标月台容量不足");
        if (!request.safetyCheckPassed()) blockers.add("入园安全检查未通过");
        if (request.hazmatPermitRequired() && !request.hazmatPermitPresent()) blockers.add("危险品许可缺失");
        if (!request.auditReady()) blockers.add("车辆调度审计证据不完整");
        if (!request.appointmentValid()) actions.add("重新确认预约时窗");
        if (!request.cargoDocsComplete()) actions.add("补齐货运单证");
        if (!request.dockAssigned()) actions.add("分配作业月台");
        if (!request.sealVerified()) actions.add("核验车辆或货柜封签");
        if (!request.operatorApproved()) actions.add("完成现场调度确认");
        var decision = !blockers.isEmpty() ? Decision.BLOCKED : actions.isEmpty() ? Decision.AUTHORIZE : Decision.DISPATCH;
        return new Result(decision, List.copyOf(blockers), List.copyOf(actions));
    }

    public enum Decision { AUTHORIZE, DISPATCH, BLOCKED }
    public record Request(String visitId, boolean appointmentValid, boolean driverVerified,
                          boolean vehicleQualified, boolean cargoDocsComplete, boolean dockAssigned,
                          boolean dockCapacityAvailable, boolean safetyCheckPassed,
                          boolean hazmatPermitRequired, boolean hazmatPermitPresent,
                          boolean sealVerified, boolean operatorApproved, boolean auditReady) {}
    public record Result(Decision decision, List<String> blockers, List<String> actions) {}
}
