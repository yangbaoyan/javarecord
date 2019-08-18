package com.test.auto.daoImpl;

import com.test.auto.bean.ColumnStruct;
import com.test.auto.bean.TableStruct;
import com.test.auto.dao.GetTablesDao;
import com.test.auto.dao.MapperXmlAutoDao;
import com.test.auto.util.*;

import java.util.List;

/**
 * 生成Mapper.xml的dao层实现类
 * @author
 *
 */
public class MapperXmlAutoDaoImpl implements MapperXmlAutoDao {
    //从GetTablesDaoImpl中获得装有所有表结构的List
    GetTablesDao getTables = new GetTablesDaoImpl();
    List<TableStruct> list = getTables.getTablesStruct();

    //通过表名、字段名称、字段类型创建Mapper.xml
    public boolean createMapperXml() {
//获得配置文件的参数
//项目路径
        String projectPath = ConfigUtil.projectPath;
//是否生成Mapper.xml
        String mapperXmlFalg=ConfigUtil.mapperXmlFlag;
//Mapper.xml的包名
        String mapperXmlPackage=ConfigUtil.mapperXmlPackage;
//Bean实体类的包名
        String beanPackage=ConfigUtil.beanPackage;
//Dao接口的包名
        String daoPackage=ConfigUtil.daoPackage;
        if("true".equals(mapperXmlFalg) ){
//将包名com.xxx.xxx形式，替换成com/xxx/xxx形成
            String mapperXmlPath=mapperXmlPackage.replace(".", "/");
//Mapper.xml的路径
            String path =projectPath+"/src/"+mapperXmlPath;
//遍历装有所有表结构的List
            for (int i = 0; i < list.size(); i++) {
//表名
                String tableName =list.get(i).getTableName();

//文件名
                String fileName=NameUtil.fileName(tableName)+"Mapper";
                String beanName =NameUtil.fileName(tableName)+"Bean";
                String daoName =NameUtil.fileName(tableName)+"Dao";

//获得每个表的所有列结构
                List<ColumnStruct> columns =list.get(i).getColumns();

//主键名
                String beanIdName=NameUtil.columnName(columns.get(0).getColumnName());
                String IdName = columns.get(0).getColumnName();
//主键类型
                String IdType = DataTypeUtil.getType(columns.get(0).getDataType());
                String IdParamType =ParamTypeUtil.getParamType(IdType);
                String IdJdbcType = JdbcTypeUtil.getJdbcType(IdType);
                if(IdJdbcType=="INT"||"INT".equals(IdJdbcType)){
                    IdJdbcType="INTEGER";
                }

//(Mapper.xml）文件内容
                StringBuffer headCon = new StringBuffer();
                headCon.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
                headCon.append("<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
                headCon.append("<mapper namespace=\""+daoPackage+"."+daoName+"\">\n");

                StringBuffer resultMapCon = new StringBuffer();
                resultMapCon.append("\t"+"<resultMap id=\"BaseResultMap\" type=\""+beanPackage+"."+beanName+"\">\n");

                StringBuffer baseColCon = new StringBuffer();
                baseColCon.append("\t"+"<sql id=\"Base_Column_List\">\n");

                StringBuffer insertRecordCon = new StringBuffer();
                insertRecordCon.append("\t"+"<insert id=\"insertRecord\" parameterType=\""+beanPackage+"."+beanName+"\">\n");
                insertRecordCon.append("\t\t"+"insert into "+tableName+"(");

                StringBuffer insertRecordCons = new StringBuffer();
                insertRecordCons.append("\t\t"+"values (");

                StringBuffer insertSelCon = new StringBuffer();
                insertSelCon.append("\t"+"<insert id=\"insertSelective\" parameterType=\""+beanPackage+"."+beanName+"\">\n");
                insertSelCon.append("\t\t"+"insert into "+tableName+"\n");
                insertSelCon.append("\t\t"+"<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\" >\n");

                StringBuffer insertSelCons = new StringBuffer();
                StringBuffer delByIdCon = new StringBuffer();
                delByIdCon.append("\t"+"<delete id=\"deleteById\" parameterType=\""+IdParamType+"\">\n");
                delByIdCon.append("\t\t"+"delete from "+tableName+" where "+IdName+"= #{"+beanIdName+",jdbcType="+IdJdbcType+"}\n");
                delByIdCon.append("\t"+"</delete>\n");

                StringBuffer updateByIdSelCon = new StringBuffer();
                updateByIdSelCon.append("\t"+"<update id=\"updateByIdSelective\" parameterType=\""+beanPackage+"."+beanName+"\">\n");
                updateByIdSelCon.append("\t\t"+"update "+tableName+"\n"+"\t\t"+"<set>\n");

                StringBuffer updateByIdCon = new StringBuffer();
                updateByIdCon.append("\t"+"<update id=\"updateById\" parameterType=\""+beanPackage+"."+beanName+"\">\n");
                updateByIdCon.append("\t\t"+"update "+tableName+" set\n");

                StringBuffer countRecordCon = new StringBuffer();
                countRecordCon.append("\t"+"<select id=\"countRecord\" resultType=\"java.lang.Integer\">\n");
                countRecordCon.append("\t\t"+"select count(*) from "+tableName+"\n");
                countRecordCon.append("\t"+"</select>\n");

                StringBuffer countSelCon = new StringBuffer();
                countSelCon.append("\t"+"<select id=\"countSelective\" parameterType=\""+beanPackage+"."+beanName+"\" resultType=\"java.lang.Integer\">\n");
                countSelCon.append("\t\t"+"select count(*) from "+tableName+" where 1=1\n");

                StringBuffer maxIdCon = new StringBuffer();
                maxIdCon.append("\t"+"<select id=\"maxId\" resultType=\"java.lang.Integer\">\n");
                maxIdCon.append("\t\t"+"select max("+IdName+") from "+tableName+"\n");
                maxIdCon.append("\t"+"</select>\n");

                StringBuffer selectByIdCon = new StringBuffer();
                selectByIdCon.append("\t"+"<select id=\"selectById\" parameterType=\""+IdParamType+"\" resultMap=\"BaseResultMap\">\n");
                selectByIdCon.append("\t\t"+"select\n"+"\t\t"+"<include refid=\"Base_Column_List\"/>\n");
                selectByIdCon.append("\t\t"+"from "+tableName+"\n"+"\t\t"+"where "+IdName+"= #{"+beanIdName+",jdbcType="+IdJdbcType+"}\n");
                selectByIdCon.append("\t"+"</select>\n");

                StringBuffer selectAllCon=new StringBuffer();
                selectAllCon.append("\t"+"<select id=\"selectAll\" resultMap=\"BaseResultMap\">\n");
                selectAllCon.append("\t\t"+"select * from "+tableName+"\n");
                selectAllCon.append("\t"+"</select>\n");

//遍历List，将字段名称和字段类型、属性名写进文件
                for (int j = 0; j <columns.size(); j++) {
//字段名
                    String columnName =columns.get(j).getColumnName();
//属性（变量）名
                    String attrName =NameUtil.columnName(columns.get(j).getColumnName());
//字段类型
                    String type=DataTypeUtil.getType(columns.get(j).getDataType());
                    String jdbcType =JdbcTypeUtil.getJdbcType(type);
                    if(jdbcType=="INT"||"INT".equals(jdbcType)){
                        jdbcType="INTEGER";
                    }
                    if(j==0){
                        resultMapCon.append("\t\t"+"<id column=\""+columnName+"\" property=\""+attrName+"\" jdbcType=\""+jdbcType+"\"/>\n");
                        baseColCon.append("\t\t"+columnName);
                        insertRecordCon.append(columnName);
                        insertRecordCons.append("#{"+attrName+",jdbcType="+jdbcType+"}");
                    }else{
                        resultMapCon.append("\t\t"+"<result column=\""+columnName+"\" property=\""+attrName+"\" jdbcType=\""+jdbcType+"\"/>\n");
                        baseColCon.append(","+columnName);
                        insertRecordCon.append(",\n"+"\t\t\t"+columnName);
                        insertRecordCons.append(",\n"+"\t\t\t"+"#{"+attrName+",jdbcType="+jdbcType+"}");
                        updateByIdSelCon.append("\t\t\t"+"<if test=\""+attrName+" != null\" >\n"+"\t\t\t\t"+columnName+"="+"#{"+attrName+",jdbcType="+jdbcType+"},\n"+"\t\t\t"+"</if>\n");

                        if(j==columns.size()-1){
                            updateByIdCon.append("\t\t\t"+columnName+"="+"#{"+attrName+",jdbcType="+jdbcType+"}\n");
                        }else{
                            updateByIdCon.append("\t\t\t"+columnName+"="+"#{"+attrName+",jdbcType="+jdbcType+"},\n");
                        }
                    }
                    insertSelCon.append("\t\t\t"+"<if test=\""+attrName+" != null\" >\n"+"\t\t\t\t"+columnName+",\n"+"\t\t\t"+"</if>\n");
                    insertSelCons.append("\t\t\t"+"<if test=\""+attrName+" != null\" >\n"+"\t\t\t\t"+"#{"+attrName+",jdbcType="+jdbcType+"}"+",\n"+"\t\t\t"+"</if>\n");
                    countSelCon.append("\t\t"+"<if test=\""+attrName+" != null\" >\n"+"\t\t\t"+"and "+columnName+"="+"#{"+attrName+",jdbcType="+jdbcType+"}\n"+"\t\t"+"</if>\n");

                }
                resultMapCon.append("\t"+"</resultMap>\n");
                baseColCon.append("\n\t"+"</sql>\n");
                insertRecordCon.append(")\n");
                insertRecordCons.append(")\n"+"	"+"</insert>\n");
                insertSelCon.append("\t\t"+"</trim>\n");
                insertSelCon.append("\t\t"+"<trim prefix=\"values (\" suffix=\")\" suffixOverrides=\",\">\n");
                insertSelCons.append("\t\t"+"</trim>\n");
                insertSelCons.append("\t"+"</insert>\n");
                updateByIdSelCon.append("\t\t"+"</set>\n"+"\t\t"+"where "+IdName+"= #{"+beanIdName+",jdbcType="+IdJdbcType+"}\n"+"\t"+"</update>\n");
                updateByIdCon.append("\t\t"+"where "+IdName+"= #{"+beanIdName+",jdbcType="+IdJdbcType+"}\n"+"\t"+"</update>\n");
                countSelCon.append("\t"+"</select>\n");

//拼接(Mapper.xml）文件内容
                StringBuffer content=new StringBuffer();
                content.append(headCon);
                content.append(resultMapCon);
                content.append(baseColCon);
                content.append(insertRecordCon);
                content.append(insertRecordCons);
                content.append(insertSelCon);
                content.append(insertSelCons);
                content.append(delByIdCon);
                content.append(updateByIdSelCon);
                content.append(updateByIdCon);
                content.append(countRecordCon);
                content.append(countSelCon);
                content.append(maxIdCon);
                content.append(selectByIdCon);
                content.append(selectAllCon);
                content.append("</mapper>");

                FileUtil.createFileAtPath(path+"/", fileName+".xml", content.toString());
            }
            return true;
        }
        return false;
    }
}
