/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.yms;
import org.junit.jupiter.api.Test; import org.springframework.beans.factory.annotation.Autowired; import org.springframework.boot.test.context.SpringBootTest; import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc; import org.springframework.http.MediaType; import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic; import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc class EnterpriseYmsApiTests { @Autowired MockMvc mvc;

 @Test void compliantAppointmentCanEnterGate() throws Exception {mvc.perform(post("/api/enterprise/yms/evaluate-slot").with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content("""
 {"appointmentNo":"APT-001","dockNo":"D-01","arrival":"2026-08-26T09:10:00","windowStart":"2026-08-26T09:00:00","windowEnd":"2026-08-26T10:00:00","dockAvailable":true,"vehicleCertified":true,"loadUnits":20,"dockCapacity":30}
 """)).andExpect(status().isOk()).andExpect(jsonPath("$.data.gateAllowed").value(true)).andExpect(jsonPath("$.data.decision").value("READY_FOR_GATE"));}
 @Test void capacityAndCertificationViolationsBlockEntry() throws Exception {mvc.perform(post("/api/enterprise/yms/evaluate-slot").with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content("""
 {"appointmentNo":"APT-002","dockNo":"D-02","arrival":"2026-08-26T09:10:00","windowStart":"2026-08-26T09:00:00","windowEnd":"2026-08-26T10:00:00","dockAvailable":true,"vehicleCertified":false,"loadUnits":40,"dockCapacity":30}
 """)).andExpect(status().isOk()).andExpect(jsonPath("$.data.gateAllowed").value(false)).andExpect(jsonPath("$.data.blockers.length()").value(2));}
}

