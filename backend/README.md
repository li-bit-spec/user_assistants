# 后端目录结构与技术架构说明

## 总体结构

```
backend/
├── SQL/                # 数据库建表脚本
├── kjz-common/         # 通用基础模块（实体、配置、工具、异常、常量等）
├── peds-admin/         # 管理端服务（业务实现、接口、全局配置）
├── peds-api/           # API服务（对外接口）
├── pom.xml             # 后端多模块聚合Maven配置
```

## 技术架构
- Spring Boot 2.x
- MyBatis Plus
- Redis
- Druid 数据库连接池
- Lombok
- Spring Cloud（Eureka、Config、Ribbon、Feign）
- Swagger2（API文档）
- Joda-Time、Hutool、Fastjson、Orika、Velocity、Freemarker

## 各模块说明

### kjz-common（通用基础模块）
- **config/**：通用配置类（如 OrikaConfig.java 对象映射配置）
- **consts/**：全局常量（如 Constant.java，code/ 目录下为状态码等）
- **dao/**：通用DAO接口
- **entity/**：
  - po/：实体类（如 SysUser.java、PageInfo.java）
  - dto/：数据传输对象（如 SysUserDTO.java、SysUserQueryDTO.java）
- **exception/**：自定义异常（如 ServiceException.java）
- **generator/**：代码生成工具
- **handler/**：全局异常处理、统一响应处理（如 BaseGlobalExceptionHandler.java、BaseGlobalResponseBodyHandler.java）
- **pojo/**：通用返回对象（如 CommonResult.java）
- **service/**：通用服务接口及实现
- **util/**：工具类（如 PageInfoUtil.java）

#### 典型通用配置文件
- `config/OrikaConfig.java`：对象属性映射配置，便于DTO与PO互转。
- `consts/Constant.java`：全局常量定义。
- `handler/BaseGlobalExceptionHandler.java`：全局异常处理，统一错误响应。
- `handler/BaseGlobalResponseBodyHandler.java`：统一响应体处理。
- `pojo/CommonResult.java`：统一API返回结构。
- `exception/ServiceException.java`：自定义业务异常。
- `util/PageInfoUtil.java`：分页工具类。

### peds-admin（管理端服务）
- **global/**：全局配置（如 SwaggerConfig、拦截器配置）
- **module/system/**：系统管理相关（controller、service、dto、req、response）
- **util/**：业务工具类
- **AdminApplication.java**：Spring Boot 启动类

### peds-api（API服务）
- 主要用于对外暴露接口，依赖 kjz-common
- **ApiApplication.java**：Spring Boot 启动类

## 说明
- 所有模块均采用Maven多模块聚合管理，依赖统一在父pom中声明。
- kjz-common为所有业务模块提供基础能力和通用配置，极大提升开发效率和一致性。
- 推荐优先在kjz-common中扩展通用能力，再在业务模块中按需实现。 