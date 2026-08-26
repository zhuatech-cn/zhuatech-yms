/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.yms.config;

import cn.zhuatech.yms.domain.DomainCatalog;
import cn.zhuatech.yms.model.*;
import cn.zhuatech.yms.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seed(BusinessRecordRepository records, SystemSettingRepository settings, DomainCatalog catalog) {
        return args -> {
            if (records.count() > 0) return;
        settings.save(new SystemSetting("appointmentGraceMinutes", "15"));
        settings.save(new SystemSetting("dockSlotMinutes", "60"));
        settings.save(new SystemSetting("vehicleCertificateRequired", "true"));
        settings.save(new SystemSetting("overstayBilling", "启用"));
            int sequence = 1;
            for (var module : catalog.modules()) {
                String no = "YMS-DEMO-" + String.format("%03d", sequence);
                records.save(new BusinessRecord(
                    no, module.code(), module.name() + "标准业务事项", "上海总部",
                    sequence % 3 == 0 ? "内控经理" : "业务专员", catalog.initialStatus(),
                    BigDecimal.valueOf(sequence * 12500L), sequence * 2,
                    LocalDate.now().plusDays(sequence * 3L), sequence % 4 == 0 ? "关注" : "正常",
                    module.description() + "；用于演示完整台账、状态流、权限和审计能力"));
                sequence++;
            }
        };
    }
}

