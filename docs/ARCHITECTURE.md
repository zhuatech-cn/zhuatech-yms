# 园区与月台管理 YMS架构说明

```text
Vue 3 响应式业务工作台
          │ HTTPS / JSON
Spring Security（ADMIN / OPERATOR）
          │
Controller → YmsService → JPA → MySQL 8
    │              ├─ 0 个领域模块
    │              ├─ 可校验状态机与领域规则
    │              └─ 统一异常、审计、配置
    └─ EnterpriseControlService
                   ├─ 幂等与乐观锁
                   ├─ 职责分离和附件摘要
                   └─ 办结与外部适配器回执
```

## 设计边界

- Java 根包：`cn.zhuatech.yms`。
- 领域目录是模块和状态流的单一事实源，服务端执行所有关键校验。
- 前端不保存真实凭据；生产认证可替换为企业 IAM/OIDC 适配器。
- 附件实体由对象存储承载，数据库仅保存不可抵赖摘要元数据。
- WMS、TMS、地磅、车牌识别与叫号屏 通过适配器接入，社区版不包含厂商绑定和密钥。

公司官网：[https://www.zhuatech.cn/](https://www.zhuatech.cn/)

