package com.test.auto.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 获得properties配置文件的参数工具类
 * @author
 *
 */
public class ConfigUtil {

    //项目路径的参数
    public static String projectPath;
    //生成Bean实体类的参数
    public static String beanFlag;
    public static String beanPackage;
    //生成Dao接口的参数
    public static String daoFlag;
    public static String daoPackage;
    //生成Service接口的参数
    public static String serviceFlag;
    public static String servicePackage;
    //生成Mapper.xml的参数
    public static String mapperXmlFlag;
    public static String mapperXmlPackage;
    //生成ServiceImpl实现类的参数
    public static String serviceImplFlag;
    public static String serviceImplPackage;
    //获取配置文件参数并加载驱动
    static{
        try {
//得到配置文件的流信息
            InputStream in = DataSourceUtil.class.getClassLoader().getResourceAsStream("config.properties");
//加载properties文件的工具类
            Properties pro = new Properties();
//工具类去解析配置文件的流信息
            pro.load(in);
//将文件得到的信息,赋值到全局变量
            projectPath = pro.getProperty("projectPath");
            beanFlag =pro.getProperty("beanFlag");
            beanPackage = pro.getProperty("beanPackage");
            daoFlag =pro.getProperty("daoFlag");
            daoPackage = pro.getProperty("daoPackage");
            serviceFlag=pro.getProperty("serviceFlag");
            servicePackage=pro.getProperty("servicePackage");
            mapperXmlFlag =pro.getProperty("mapperXmlFlag");
            mapperXmlPackage = pro.getProperty("mapperXmlPackage");
            serviceImplFlag=pro.getProperty("serviceImplFlag");
            serviceImplPackage=pro.getProperty("serviceImplPackage");
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
