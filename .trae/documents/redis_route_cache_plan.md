# Tigodaimoz - Redis 路由缓存实施计划

## 项目现状分析
- 当前路由配置存储在PostgreSQL数据库中
- `DatabaseRouteDefinitionLocator` 每次调用 `getRouteDefinitions()` 都会从数据库查询所有路由
- 路由配置转换过程包括JSON解析，可能影响性能
- 缺少缓存机制，每次请求都需要访问数据库

## 实施计划

### [ ] 任务1：添加Redis依赖
- **优先级**：P0
- **依赖**：None
- **描述**：
  - 在tigor-gateway模块的build.gradle中添加Spring Boot Redis依赖
  - 确保依赖版本与Spring Boot 4.0.3兼容
- **成功标准**：
  - 依赖添加成功，项目能够正常构建
- **测试要求**：
  - `programmatic` TR-1.1：执行 `./gradlew build` 构建成功
- **注意事项**：
  - 使用与项目其他模块一致的Redis依赖版本

### [ ] 任务2：创建Redis缓存服务
- **优先级**：P0
- **依赖**：任务1
- **描述**：
  - 创建 `RouteCacheService` 类，负责路由配置的Redis缓存操作
  - 实现缓存的存储、读取和更新方法
  - 定义缓存键的命名规则
- **成功标准**：
  - `RouteCacheService` 类创建完成
  - 缓存方法实现正确
- **测试要求**：
  - `programmatic` TR-2.1：缓存服务能够正常编译
  - `human-judgement` TR-2.2：代码结构清晰，注释完整
- **注意事项**：
  - 考虑缓存过期策略
  - 处理Redis连接异常

### [ ] 任务3：修改DatabaseRouteDefinitionLocator
- **优先级**：P0
- **依赖**：任务2
- **描述**：
  - 修改 `getRouteDefinitions()` 方法，优先从Redis读取路由配置
  - 如果Redis中没有缓存，则从数据库读取并缓存到Redis
  - 添加缓存更新机制
- **成功标准**：
  - 路由配置能够从Redis读取
  - 当Redis中没有缓存时，能够从数据库读取并缓存
- **测试要求**：
  - `programmatic` TR-3.1：项目能够正常构建
  - `human-judgement` TR-3.2：代码逻辑正确，缓存策略合理
- **注意事项**：
  - 确保缓存与数据库数据一致性
  - 考虑并发访问的情况

### [ ] 任务4：添加路由配置更新机制
- **优先级**：P1
- **依赖**：任务3
- **描述**：
  - 实现路由配置更新时的缓存同步机制
  - 可以通过事件监听或定时任务实现
- **成功标准**：
  - 当数据库中的路由配置发生变化时，Redis缓存能够及时更新
- **测试要求**：
  - `programmatic` TR-4.1：项目能够正常构建
  - `human-judgement` TR-4.2：更新机制设计合理
- **注意事项**：
  - 避免缓存不一致
  - 考虑更新频率和性能影响

### [ ] 任务5：测试和验证
- **优先级**：P1
- **依赖**：任务4
- **描述**：
  - 测试路由缓存的性能提升
  - 验证缓存与数据库数据的一致性
  - 测试路由更新时的缓存同步
- **成功标准**：
  - 路由加载性能明显提升
  - 缓存与数据库数据保持一致
  - 路由更新后缓存能够正确同步
- **测试要求**：
  - `programmatic` TR-5.1：执行 `./gradlew build` 构建成功
  - `human-judgement` TR-5.2：路由加载速度明显快于之前
- **注意事项**：
  - 测试不同场景下的性能表现
  - 验证异常情况下的处理

## 技术实现细节

### Redis缓存键设计
- 路由配置缓存键：`gateway:routes:all`
- 单个路由缓存键：`gateway:route:{routeId}`

### 缓存策略
1. 首次加载时从数据库读取并缓存到Redis
2. 后续加载优先从Redis读取
3. 当数据库路由配置发生变化时，更新Redis缓存

### 数据序列化
- 使用Jackson将RouteDefinition对象序列化为JSON存储到Redis
- 从Redis读取时反序列化为RouteDefinition对象

## 预期成果

- 路由加载性能显著提升
- 减少数据库访问次数
- 保持路由配置的一致性
- 系统整体响应速度提高