# Tigodaimoz - Spring Boot 升级与数据库迁移计划

## 项目现状分析
- 当前Spring Boot版本：3.5.3
- 当前数据库：MariaDB
- 同时配置了JPA和R2DBC
- 多租户实现：存在CustomTenantIdentifierResolver但实现为空
- 项目结构：tigor-auth、tigor-eureka、tigor-frameworks、tigor-gateway

## 实施计划

### [ ] 任务1：升级Spring Boot版本到4.0.3
- **优先级**：P0
- **依赖**：None
- **描述**：
  - 更新根目录build.gradle中的Spring Boot版本
  - 更新所有子模块中的Spring Boot版本依赖
  - 确保兼容Java 24版本
- **成功标准**：
  - 所有模块能够正常构建
  - 项目启动无错误
- **测试要求**：
  - `programmatic` TR-1.1：执行`./gradlew build`构建成功
  - `programmatic` TR-1.2：启动所有服务无异常
- **注意事项**：
  - 检查Spring Boot 4.0.3与当前依赖的兼容性
  - 注意Spring Cloud版本的匹配

### [ ] 任务2：修改数据库从JPA改为R2DBC
- **优先级**：P0
- **依赖**：任务1
- **描述**：
  - 移除JPA相关依赖
  - 清理JPA配置
  - 确保R2DBC配置正确
  - 调整数据访问层代码
- **成功标准**：
  - 项目不再依赖JPA
  - 所有数据库操作通过R2DBC执行
  - 系统功能正常
- **测试要求**：
  - `programmatic` TR-2.1：检查依赖中无JPA相关包
  - `programmatic` TR-2.2：执行数据库操作测试
  - `human-judgement` TR-2.3：代码审查确认无JPA使用
- **注意事项**：
  - 注意R2DBC与JPA的API差异
  - 确保事务处理正确

### [ ] 任务3：修改数据库为PostgreSQL
- **优先级**：P1
- **依赖**：任务2
- **描述**：
  - 替换MariaDB依赖为PostgreSQL
  - 更新数据库连接配置
  - 调整SQL语句以适配PostgreSQL
  - 确保R2DBC驱动正确配置
- **成功标准**：
  - 项目连接到PostgreSQL数据库
  - 所有数据库操作正常执行
  - 系统功能不受影响
- **测试要求**：
  - `programmatic` TR-3.1：连接PostgreSQL数据库成功
  - `programmatic` TR-3.2：执行CRUD操作测试
  - `human-judgement` TR-3.3：审查数据库配置和SQL语句
- **注意事项**：
  - 注意PostgreSQL与MariaDB的语法差异
  - 确保数据库迁移脚本正确

### [ ] 任务4：实现Schema隔离方式的多租户
- **优先级**：P1
- **依赖**：任务3
- **描述**：
  - 完善CustomTenantIdentifierResolver实现
  - 配置PostgreSQL Schema隔离
  - 实现租户切换逻辑
  - 测试多租户功能
- **成功标准**：
  - 多租户通过Schema隔离实现
  - 不同租户数据相互隔离
  - 租户切换功能正常
- **测试要求**：
  - `programmatic` TR-4.1：创建多个租户Schema
  - `programmatic` TR-4.2：测试不同租户数据隔离
  - `human-judgement` TR-4.3：审查多租户实现代码
- **注意事项**：
  - 确保Schema创建和管理正确
  - 注意租户切换的性能影响

## 实施步骤

1. **准备阶段**：
   - 备份当前项目
   - 了解Spring Boot 4.0.3的变更
   - 熟悉PostgreSQL的R2DBC配置

2. **执行阶段**：
   - 按任务优先级顺序执行
   - 每完成一个任务进行测试验证
   - 记录遇到的问题和解决方案

3. **验证阶段**：
   - 执行完整的系统测试
   - 验证所有功能正常
   - 确认多租户隔离效果

## 风险评估

- **依赖兼容性风险**：Spring Boot 4.0.3可能与某些依赖不兼容
- **数据库迁移风险**：从MariaDB到PostgreSQL可能存在数据类型和SQL语法差异
- **多租户实现风险**：Schema隔离方式可能需要额外的配置和管理

## 预期成果

- 项目成功升级到Spring Boot 4.0.3
- 数据库从JPA完全迁移到R2DBC
- 数据库系统切换到PostgreSQL
- 多租户实现采用Schema隔离方式
- 系统功能保持完整且性能稳定