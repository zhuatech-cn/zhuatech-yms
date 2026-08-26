/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.yms.domain;

import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class DomainCatalog {
    private final Map<String, WorkflowAction> actions = new LinkedHashMap<>();

    public DomainCatalog() {
        actions.put("CHECK_IN", new WorkflowAction("CHECK_IN", "门岗入园", List.of("已预约"), "已入园", "OPERATOR"));
        actions.put("QUEUE", new WorkflowAction("QUEUE", "进入排队", List.of("已入园"), "排队中", "OPERATOR"));
        actions.put("ASSIGN_DOCK", new WorkflowAction("ASSIGN_DOCK", "分配月台", List.of("排队中"), "作业中", "OPERATOR"));
        actions.put("COMPLETE", new WorkflowAction("COMPLETE", "完成作业", List.of("作业中"), "已完成", "OPERATOR"));
        actions.put("CANCEL", new WorkflowAction("CANCEL", "取消预约", List.of("已预约"), "已取消", "ADMIN"));
    }

    public String systemName() { return "知华科技园区与月台管理 YMS"; }
    public String scene() { return "车辆预约、门岗、排队、月台调度、称重、装卸、异常与计费"; }
    public String initialStatus() { return "已预约"; }
    public String partyLabel() { return "车辆/承运商"; }
    public String amountLabel() { return "作业费用"; }
    public String quantityLabel() { return "车次/托盘数"; }
    public String dueLabel() { return "预约时段"; }

    public List<ModuleDefinition> modules() {
        return List.of(
            new ModuleDefinition("SITE", "园区主数据", "管理园区、门岗、停车区、作业区和通行规则"),
            new ModuleDefinition("CARRIER", "承运商车辆", "维护承运商、司机、车辆、证照和黑名单"),
            new ModuleDefinition("APPOINTMENT", "车辆预约", "管理时窗、货物、优先级和预约容量"),
            new ModuleDefinition("GATE", "门岗管理", "完成身份核验、车牌识别、入离园和安检"),
            new ModuleDefinition("QUEUE", "排队叫号", "根据优先级、到达时间和月台能力动态排队"),
            new ModuleDefinition("DOCK", "月台调度", "分配月台、装卸资源并防止时段冲突"),
            new ModuleDefinition("WEIGHING", "称重管理", "记录皮重、毛重、净重与磅单凭证"),
            new ModuleDefinition("LOADING", "装卸作业", "跟踪开始、暂停、完成、数量和作业签收"),
            new ModuleDefinition("EXCEPTION", "异常管理", "处理迟到、拒收、破损、拥堵和安全事件"),
            new ModuleDefinition("BILLING", "费用结算", "计算等待、装卸、停车和异常费用")
        );
    }

    public Map<String, WorkflowAction> actions() { return Collections.unmodifiableMap(actions); }

    public record ModuleDefinition(String code, String name, String description) {}
    public record WorkflowAction(String code, String label, List<String> from, String to, String requiredRole) {}
}
