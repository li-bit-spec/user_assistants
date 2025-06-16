package com.kjz.common.generator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.FileType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.io.File;

public class CodeGenerator {


        public static void main(String[] args) {

            // 1、创建代码生成器
            AutoGenerator mpg = new AutoGenerator();

            // 2、全局配置
            GlobalConfig gc = new GlobalConfig();
            String projectPath = System.getProperty("user.dir");
            gc.setOutputDir(projectPath + "/kjz-common/src/main/java");
            gc.setAuthor("zhangcheng");
            gc.setOpen(false); //生成后是否打开资源管理器
            gc.setFileOverride(false); //重新生成时文件是否覆盖
            gc.setServiceName("%sService"); //去掉Service接口的首字母I
            gc.setMapperName("%sDao");
            gc.setIdType(IdType.AUTO); //主键策略
            gc.setDateType(DateType.ONLY_DATE);//定义生成的实体类中日期类型
            gc.setSwagger2(false);//开启Swagger2模式

            mpg.setGlobalConfig(gc);

            // 3、数据源配置
            DataSourceConfig dsc = new DataSourceConfig();
            dsc.setUrl("jdbc:mysql://39.105.143.94:3366/db_kjz_peds?useUnicode=true&useSSL=false&characterEncoding=utf8");
            dsc.setDriverName("com.mysql.jdbc.Driver");
            dsc.setUsername("kjz_peds");
            dsc.setPassword("iA1^cE5%cL");
            dsc.setDbType(DbType.MYSQL);
            mpg.setDataSource(dsc);

            // 4、包配置
            PackageConfig pc = new PackageConfig();
            pc.setModuleName(null); //模块名
            pc.setParent("com.kjz.common");
            pc.setEntity("entity");
            pc.setService("service");
            pc.setMapper("dao");
            mpg.setPackageInfo(pc);

            // 5、策略配置
            StrategyConfig strategy = new StrategyConfig();
            strategy.setNaming(NamingStrategy.underline_to_camel);
            strategy.setColumnNaming(NamingStrategy.underline_to_camel);
            strategy.setEntityLombokModel(true);
            strategy.setRestControllerStyle(true);
            strategy.setEntityTableFieldAnnotationEnable(true);
            strategy.setInclude(
                    "sys_user"
            );
            strategy.setControllerMappingHyphenStyle(true);
            strategy.setTablePrefix(pc.getModuleName() + "_");

            // 自定义配置
            InjectionConfig cfg = new InjectionConfig() {
                @Override
                public void initMap() {
                    // to do nothing
                }
            };
            cfg.setFileCreate(new IFileCreate() {
                @Override
                public boolean isCreate(ConfigBuilder configBuilder, FileType fileType, String filePath) {
                    // 判断自定义文件夹是否需要创建
                    if (fileType == FileType.ENTITY) {
                        // entity 永远覆盖生成
                        return true;
                    }
                    // 不生成service、controller
                    if (fileType == FileType.XML  || fileType == FileType.CONTROLLER) {
                        return false;
                    }
                    // 判断文件是否存在
                    return !new File(filePath).exists();
                }
            });
            mpg.setCfg(cfg);
            mpg.setStrategy(strategy);
// 配置模板
            TemplateConfig templateConfig = new TemplateConfig();
            templateConfig.setXml(null);
            mpg.setTemplate(templateConfig);
            // 6、执行
            mpg.execute();
        }

}
