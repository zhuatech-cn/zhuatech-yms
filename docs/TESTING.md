# 园区与月台管理 YMS测试说明

## 自动化范围

- 公开元数据、未认证拦截、ADMIN/OPERATOR 权限边界；
- 0 个模块目录、仪表板与演示数据；
- 创建、草稿修改、删除、未知模块、非法越级和正常状态流；
- 经办/管理员流程动作权限、组合检索、分页、CSV 导出、SLA 看板；
- 单据详情、协作备注和操作时间线；
- 持久化设置、操作审计、幂等创建、职责分离；
- 附件 SHA-256、无凭证禁止办结、外部同步回执；
- 预约时窗、月台可用性、车辆资质与作业容量联合校验的正常、异常与边界场景。

## 执行

```bash
mvn -f backend/pom.xml test
npm ci --prefix frontend
npm run build --prefix frontend
docker compose config
```

后端使用 H2 MySQL 兼容模式隔离测试；前端以 Vite production build 验证模板、依赖和静态资源。CI 定义位于 `.github/workflows/ci.yml`。

维护方：上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
