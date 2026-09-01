/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.yms.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class YardMovementAuthorizationServiceTest {
    private final YardMovementAuthorizationService service = new YardMovementAuthorizationService();

    @Test void authorizesSafeAndPreparedMovement() {
        var result = service.assess(new YardMovementAuthorizationService.Request("VISIT-100", true, true,
                true, true, true, true, true, false, true, true, true, true));
        assertThat(result.decision()).isEqualTo(YardMovementAuthorizationService.Decision.AUTHORIZE);
    }

    @Test void routesOperationalGapsToDispatch() {
        var result = service.assess(new YardMovementAuthorizationService.Request("VISIT-101", false, true,
                true, false, false, true, true, false, true, false, false, true));
        assertThat(result.actions()).hasSize(5);
        assertThat(result.decision()).isEqualTo(YardMovementAuthorizationService.Decision.DISPATCH);
    }

    @Test void blocksUnsafeYardMovement() {
        var result = service.assess(new YardMovementAuthorizationService.Request("", false, false,
                false, false, false, false, false, true, false, false, false, false));
        assertThat(result.blockers()).hasSize(7);
        assertThat(result.decision()).isEqualTo(YardMovementAuthorizationService.Decision.BLOCKED);
    }
}
