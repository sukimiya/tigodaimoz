# Tigor项目 - PostgreSQL数据库迁移与多租户Schema隔离实现计划

## 项目现状分析

### 当前数据库配置
- **数据库类型**：MariaDB
- **连接配置**：
  - 认证服务：`jdbc:mariadb://mariadb:3306/tigor`
  - 网关服务：`jdbc:mariadb://mariadb:3306/tigor`
- **多租户实现**：
  - 存在`CustomTenantIdentifierResolver`类，但为空实现
  - 未发现其他多租户相关代码

### 目标
1. 将数据库从MariaDB迁移到PostgreSQL
2. 实现基于Schema的多租户隔离方式

## 实施计划

### [ ] 任务1：更新项目依赖，添加PostgreSQL支持
- **Priority**：P0
- **Depends On**：None
- **Description**：
  - 修改`build.gradle`文件，添加PostgreSQL驱动依赖
  - 移除MariaDB驱动依赖
- **Success Criteria**：
  - 项目依赖中包含PostgreSQL驱动
  - 成功构建项目
- **Test Requirements**：
  - `programmatic` TR-1.1：运行`./gradlew build`命令，构建成功
  - `human-judgement` TR-1.2：检查构建输出，确认PostgreSQL依赖已正确添加
- **Notes**：需要确保使用兼容的PostgreSQL驱动版本

### [ ] 任务2：修改认证服务数据库配置
- **Priority**：P0
- **Depends On**：任务1
- **Description**：
  - 修改`tigor-auth/src/main/resources/application.yml`文件
  - 将数据库URL、驱动类名、方言等配置修改为PostgreSQL
- **Success Criteria**：
  - 认证服务配置文件中的数据库配置已更新为PostgreSQL
- **Test Requirements**：
  - `programmatic` TR-2.1：检查配置文件中的数据库URL是否为PostgreSQL格式
  - `human-judgement` TR-2.2：确认所有相关配置项已正确更新
- **Notes**：PostgreSQL连接URL格式：`jdbc:postgresql://localhost:5432/tigor`

### [ ] 任务3：修改网关服务数据库配置
- **Priority**：P0
- **Depends On**：任务1
- **Description**：
  - 修改`tigor-gateway/src/main/resources/application.yml`文件
  - 将数据库URL、驱动类名、方言等配置修改为PostgreSQL
- **Success Criteria**：
  - 网关服务配置文件中的数据库配置已更新为PostgreSQL
- **Test Requirements**：
  - `programmatic` TR-3.1：检查配置文件中的数据库URL是否为PostgreSQL格式
  - `human-judgement` TR-3.2：确认所有相关配置项已正确更新
- **Notes**：与任务2类似，确保使用正确的PostgreSQL连接URL格式

### [ ] 任务4：实现PostgreSQL Schema多租户隔离
- **Priority**：P1
- **Depends On**：任务2, 任务3
- **Description**：
  - 完善`CustomTenantIdentifierResolver`类，实现基于Schema的租户解析
  - 添加租户上下文管理
  - 实现租户Schema的创建和切换逻辑
- **Success Criteria**：
  - 多租户解析器能够正确解析租户ID
  - 能够根据租户ID切换到对应的Schema
  - 租户Schema不存在时能够自动创建
- **Test Requirements**：
  - `programmatic` TR-4.1：编写单元测试，验证租户解析和Schema切换功能
  - `human-judgement` TR-4.2：检查代码实现是否符合最佳实践
- **Notes**：需要考虑Schema的初始化和迁移策略

### [ ] 任务5：更新JPA配置，支持Schema多租户
- **Priority**：P1
- **Depends On**：任务4
- **Description**：
  - 修改JPA配置，启用多租户支持
  - 配置多租户策略为Schema隔离
- **Success Criteria**：
  - JPA配置已启用多租户支持
  - 多租户策略已设置为Schema隔离
- **Test Requirements**：
  - `programmatic` TR-5.1：检查JPA配置是否正确设置
  - `human-judgement` TR-5.2：确认配置项符合PostgreSQL Schema多租户要求
- **Notes**：需要在application.yml中添加多租户相关配置

### [ ] 任务6：测试数据库迁移和多租户功能
- **Priority**：P1
- **Depends On**：任务5
- **Description**：
  - 启动PostgreSQL数据库
  - 运行应用程序，测试数据库连接
  - 测试多租户功能，包括租户创建、Schema切换等
- **Success Criteria**：
  - 应用程序能够成功连接到PostgreSQL数据库
  - 多租户功能正常工作，不同租户的数据隔离在不同Schema中
- **Test Requirements**：
  - `programmatic` TR-6.1：应用程序启动成功，无数据库连接错误
  - `programmatic` TR-6.2：测试多租户API，验证数据隔离效果
  - `human-judgement` TR-6.3：检查PostgreSQL数据库中是否创建了对应的Schema
- **Notes**：需要确保PostgreSQL数据库已正确安装和配置

### [ ] 任务7：优化和文档
- **Priority**：P2
- **Depends On**：任务6
- **Description**：
  - 优化多租户实现，提高性能和可靠性
  - 编写相关文档，说明多租户实现方式和使用方法
- **Success Criteria**：
  - 多租户实现已优化，性能良好
  - 文档已编写完成，包含必要的使用说明
- **Test Requirements**：
  - `programmatic` TR-7.1：性能测试，确保多租户操作响应时间合理
  - `human-judgement` TR-7.2：检查文档是否清晰完整
- **Notes**：可以考虑添加租户管理API，方便租户的创建和管理

## 技术要点

### PostgreSQL Schema多租户实现
- **Schema隔离**：每个租户使用独立的Schema，实现数据隔离
- **租户解析**：通过请求上下文或JWT token解析租户ID
- **Schema管理**：自动创建和切换租户Schema
- **JPA配置**：使用Hibernate的多租户支持，配置为Schema隔离策略

### 数据库迁移注意事项
- **数据迁移**：需要将现有MariaDB数据迁移到PostgreSQL
- **SQL语法差异**：注意MariaDB和PostgreSQL的SQL语法差异
- **索引和约束**：确保PostgreSQL中的索引和约束与原数据库一致

### 性能优化
- **连接池配置**：合理配置PostgreSQL连接池
- **Schema缓存**：缓存Schema信息，减少Schema切换开销
- **查询优化**：优化多租户查询，确保性能良好

## 风险评估

### 潜在风险
1. **数据迁移风险**：MariaDB到PostgreSQL的数据迁移可能存在兼容性问题
2. **多租户实现复杂度**：Schema隔离方式需要额外的管理和维护
3. **性能影响**：多租户查询可能会对性能产生一定影响

### 缓解措施
1. **数据迁移测试**：在生产环境迁移前，进行充分的测试和验证
2. **渐进式实现**：先在非生产环境测试多租户功能，再逐步推广
3. **性能监控**：实施性能监控，及时发现和解决性能问题

## 预期成果

1. **数据库迁移完成**：成功将数据库从MariaDB迁移到PostgreSQL
2. **多租户功能实现**：基于Schema的多租户隔离方式正常工作
3. **系统稳定性**：应用程序能够稳定运行，无数据库相关错误
4. **性能良好**：多租户操作性能满足要求

## 时间估计

| 任务 | 估计时间 |
|------|----------|
| 任务1：更新项目依赖 | 0.5天 |
| 任务2：修改认证服务配置 | 0.5天 |
| 任务3：修改网关服务配置 | 0.5天 |
| 任务4：实现多租户隔离 | 2天 |
| 任务5：更新JPA配置 | 0.5天 |
| 任务6：测试验证 | 1天 |
| 任务7：优化和文档 | 0.5天 |
| **总计** | **5.5天** |