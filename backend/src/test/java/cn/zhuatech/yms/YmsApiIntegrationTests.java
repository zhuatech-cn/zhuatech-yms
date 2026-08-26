/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.yms;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import java.util.regex.Pattern;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc
class YmsApiIntegrationTests {
 @Autowired MockMvc mvc;

 @Test void publicMetadataSecurityCatalogAndDashboardAreAvailable() throws Exception {
  mvc.perform(get("/api/public/about")).andExpect(status().isOk()).andExpect(jsonPath("$.data.company").value("上海如静知华信息科技有限公司")).andExpect(jsonPath("$.data.website").value("https://www.zhuatech.cn/"));
  mvc.perform(get("/actuator/health")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));
  mvc.perform(get("/api/dashboard")).andExpect(status().isUnauthorized());
  mvc.perform(get("/api/catalog").with(httpBasic("operator","operator123"))).andExpect(status().isOk()).andExpect(jsonPath("$.data.modules.length()").value(10)).andExpect(jsonPath("$.data.actions[0].requiredRole").isString());
  mvc.perform(get("/api/dashboard").with(httpBasic("operator","operator123"))).andExpect(status().isOk()).andExpect(jsonPath("$.data.totalRecords").isNumber());
 }

 @Test void recordFollowsConfiguredWorkflowAndWritesAudit() throws Exception {
  long id=create("UT-YMS-001","企业流程自动化验收");
  mvc.perform(post("/api/records/{id}/actions",id).with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"QUEUE\",\"remark\":\"越级操作\"}")).andExpect(status().isConflict());
  mvc.perform(post("/api/records/{id}/actions",id).with(httpBasic("admin","admin123")).contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"CHECK_IN\",\"remark\":\"第一步完成\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("已入园"));
  mvc.perform(post("/api/records/{id}/actions",id).with(httpBasic("admin","admin123")).contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"QUEUE\",\"remark\":\"第二步完成\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.data.status").value("排队中"));
  mvc.perform(get("/api/admin/audit-logs").with(httpBasic("admin","admin123"))).andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").isNotEmpty());
 }

 @Test void draftRecordSupportsUpdateDeleteAndModuleValidation() throws Exception {
  String body=body("UT-YMS-002","待修改记录");
  long id=idOf(mvc.perform(post("/api/records").with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk()).andReturn());
  mvc.perform(put("/api/records/{id}",id).with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content(body.replace("待修改记录","已修改记录"))).andExpect(status().isOk()).andExpect(jsonPath("$.data.title").value("已修改记录"));
  mvc.perform(delete("/api/records/{id}",id).with(httpBasic("operator","operator123"))).andExpect(status().isOk());
  mvc.perform(post("/api/records").with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content(body.replace("UT-YMS-002","UT-YMS-BAD").replace("SITE","UNKNOWN"))).andExpect(status().isBadRequest());
 }

 @Test void searchSlaTimelineCommentsAndCsvExportAreUsable() throws Exception {
  long id=create("UT-YMS-OPS","运营检索与协作记录");
  mvc.perform(get("/api/records/search").param("module","SITE").param("keyword","运营检索").param("page","0").param("size","10").with(httpBasic("operator","operator123")))
   .andExpect(status().isOk()).andExpect(jsonPath("$.data.total").value(1)).andExpect(jsonPath("$.data.items[0].id").value(id));
  mvc.perform(get("/api/records/{id}",id).with(httpBasic("operator","operator123"))).andExpect(status().isOk()).andExpect(jsonPath("$.data.recordNo").value("UT-YMS-OPS"));
  mvc.perform(post("/api/records/{id}/comments",id).with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content("{\"content\":\"已与责任部门确认处理计划\"}")).andExpect(status().isOk());
  mvc.perform(get("/api/records/{id}/timeline",id).with(httpBasic("operator","operator123"))).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].action").value("协作备注"));
  mvc.perform(get("/api/sla-summary").with(httpBasic("operator","operator123"))).andExpect(status().isOk()).andExpect(jsonPath("$.data.open").isNumber()).andExpect(jsonPath("$.data.workloadByOwner").isMap());
  mvc.perform(get("/api/records/export.csv").param("keyword","运营检索").with(httpBasic("operator","operator123"))).andExpect(status().isOk()).andExpect(header().string("Content-Disposition",containsString("records.csv"))).andExpect(content().string(containsString("UT-YMS-OPS")));
 }

 @Test void administratorWorkflowActionRejectsOperator() throws Exception {
  long id=create("UT-YMS-ROLE","管理员动作权限校验");
  mvc.perform(post("/api/records/{id}/actions",id).with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"CANCEL\",\"remark\":\"无权操作\"}")).andExpect(status().isForbidden());
  mvc.perform(post("/api/records/{id}/actions",id).with(httpBasic("admin","admin123")).contentType(MediaType.APPLICATION_JSON).content("{\"action\":\"CANCEL\",\"remark\":\"授权操作\"}")).andExpect(status().isOk());
 }

 @Test void onlyAdminCanMaintainPersistentSettings() throws Exception {
  mvc.perform(get("/api/admin/settings").with(httpBasic("operator","operator123"))).andExpect(status().isForbidden());
  mvc.perform(put("/api/admin/settings").with(httpBasic("admin","admin123")).contentType(MediaType.APPLICATION_JSON).content("{\"acceptanceMode\":\"企业级自动化测试\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.data.acceptanceMode").value("企业级自动化测试"));
 }

 private long create(String no,String title)throws Exception{return idOf(mvc.perform(post("/api/records").with(httpBasic("operator","operator123")).contentType(MediaType.APPLICATION_JSON).content(body(no,title))).andExpect(status().isOk()).andReturn());}
 private String body(String no,String title){return "{\"recordNo\":\""+no+"\",\"module\":\"SITE\",\"title\":\""+title+"\",\"businessParty\":\"上海总部\",\"owner\":\"业务专员\",\"amount\":12800,\"quantity\":3,\"dueDate\":\"2026-09-30\",\"riskLevel\":\"正常\",\"description\":\"验证主要功能、状态控制和审计\"}";}
 private long idOf(MvcResult result)throws Exception{var m=Pattern.compile("\\\"id\\\":(\\d+)").matcher(result.getResponse().getContentAsString());Assertions.assertTrue(m.find());return Long.parseLong(m.group(1));}
}
