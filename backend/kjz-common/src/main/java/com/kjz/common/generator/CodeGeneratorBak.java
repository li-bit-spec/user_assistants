package com.kjz.common.generator;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.FileType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CodeGeneratorBak {

    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static void main(String[] args) {

        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();

        //当前工程路径
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/kjz-common/src/main/java");

        //是否覆盖已有文件
        gc.setFileOverride(true);
        gc.setAuthor("cong");
        gc.setOpen(false);
        gc.setBaseColumnList(true);
        gc.setBaseResultMap(true);
        // gc.setSwagger2(true);
        gc.setDateType(DateType.ONLY_DATE);
        gc.setEnableCache(false);
        gc.setServiceName("%sService");
        gc.setMapperName("%sDao");
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://39.105.143.94:3366/db_kjz_peds?serverTimezone=GMT%2b8&useUnicode=true&characterEncoding=UTF8&useSSL=false&allowMultiQueries=true");
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername("kjz_peds");
        dsc.setPassword("iA1^cE5%cL");
        dsc.setTypeConvert(new MySqlTypeConvert() {
            @Override
            public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {

                if (fieldType.toLowerCase().contains("tinyint(1)")) {
                    fieldType = "int";
                }

                return super.processTypeConvert(globalConfig, fieldType);
            }
        });
        mpg.setDataSource(dsc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("com.kjz.common");
        pc.setEntity("entity.po");
        pc.setController("controller");
        pc.setMapper("dao");
        pc.setService("service");
        pc.setServiceImpl("service.impl");
        mpg.setPackageInfo(pc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.isEntityBooleanColumnRemoveIsPrefix();
        strategy.setNaming(NamingStrategy.underline_to_camel); //数据库表映射到实体的命名策略：驼峰命名
        strategy.setColumnNaming(NamingStrategy.underline_to_camel); //数据库表字段映射到实体的命名策略：驼峰命名
        strategy.setInclude(new String[]{"coupon_cash_instance", "coupon_goods_instance"}); //需要生成的表名
        strategy.isEntityTableFieldAnnotationEnable(); //是否生成实体时，生成字段注解
        strategy.setEntityLombokModel(true);
        mpg.setStrategy(strategy);

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

        TemplateConfig tc = new TemplateConfig();
        tc.setController(null);
        // tc.setService(null);
        // tc.setServiceImpl(null);
        tc.setEntityKt(null);
        tc.setXml(null);
        mpg.setTemplate(tc);
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());

        mpg.execute();
    }

}