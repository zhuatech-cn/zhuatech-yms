# 园区与月台管理 YMS API

Base URL：`http://localhost:8080/api`。除公开信息和健康检查外均需认证；`/api/admin/**` 仅允许管理员。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/public/about` | 产品、公司、官网与许可范围 |
| GET | `/catalog` | 0 个模块、字段语义与状态动作 |
| GET | `/dashboard` | 业务总量、金额、状态、模块和风险概览 |
| GET/POST | `/records` | 查询或创建业务记录 |
| GET | `/records/search` | 按模块、状态、风险、关键字、逾期组合检索并分页 |
| GET | `/records/export.csv` | 按当前过滤条件导出 UTF-8 CSV |
| GET | `/records/{id}` | 查询单据详情 |
| GET | `/records/{id}/timeline` | 查询创建、流程和协作备注时间线 |
| POST | `/records/{id}/comments` | 添加可审计的协作备注 |
| GET | `/sla-summary` | 在办、逾期、七日到期、高风险和责任人负荷 |
| PUT/DELETE | `/records/{id}` | 仅初始状态允许修改和删除 |
| POST | `/records/{id}/actions` | 校验前置状态并执行流程动作 |
| POST | `/enterprise/yms/evaluate-slot` | 预约时窗、月台可用性、车辆资质与作业容量联合校验 |
| GET | `/admin/audit-logs` | 最近 100 条操作审计 |
| GET/PUT | `/admin/settings` | 持久化系统参数 |
| GET | `/enterprise/summary` | 企业控制项、逾期和同步汇总 |
| GET/POST | `/enterprise/controls` | 查询或幂等创建控制项 |
| POST | `/enterprise/controls/{id}/submit` | 经办提交 |
| POST | `/admin/enterprise/controls/{id}/review` | 管理员批准或驳回 |
| POST | `/enterprise/controls/{id}/documents` | 登记附件元数据与 SHA-256 |
| POST | `/enterprise/controls/{id}/complete` | 凭证齐备后办结 |
| POST | `/admin/enterprise/controls/{id}/sync` | 写入外部系统回执 |
| GET | `/actuator/health` | 服务健康状态 |

流程：``。非法状态迁移返回 HTTP 409，输入校验失败返回 HTTP 400，未认证返回 401，越权返回 403。

维护方：上海如静知华信息科技有限公司 · [https://www.zhuatech.cn/](https://www.zhuatech.cn/)
