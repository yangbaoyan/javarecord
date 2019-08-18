package com.test.auto.daoImpl;

import com.test.auto.bean.ColumnStruct;
import com.test.auto.bean.TableStruct;
import com.test.auto.dao.GetTablesDao;
import com.test.auto.dao.ServiceImplAutoDao;
import com.test.auto.util.ConfigUtil;
import com.test.auto.util.DataTypeUtil;
import com.test.auto.util.FileUtil;
import com.test.auto.util.NameUtil;

import java.util.List;

public class ServiceImplAutoDaoImpl implements ServiceImplAutoDao {
    //从GetTablesDaoImpl中获得装有所有表结构的List
    GetTablesDao getTables = new GetTablesDaoImpl();
    List<TableStruct> list = getTables.getTablesStruct();

    //通过表名、字段名称、字段类型创建ServiceImpl实现类
    public boolean createServiceImpl() {
//获得配置文件的参数
//项目路径
        String projectPath = ConfigUtil.projectPath;
//是否生成Service
        String serviceImplFalg=ConfigUtil.serviceImplFlag;
//Service接口的包名
        String serviceImplPackage=ConfigUtil.serviceImplPackage;
//Bean实体类的包名
        String beanPackage=ConfigUtil.beanPackage;
//Service接口的包名
        String servicePackage=ConfigUtil.servicePackage;
//Dao接口的包名
        String daoPackage=ConfigUtil.daoPackage;
        if("true".equals(serviceImplFalg) ){
//将包名com.xxx.xxx形式，替换成com/xxx/xxx形成
            String serviceImplPath=serviceImplPackage.replace(".", "/");
//Service接口的路径
            String path =projectPath+"/src/"+serviceImplPath;
//遍历装有所有表结构的List
            for (int i = 0; i < list.size(); i++) {
//文件名
                String fileName=NameUtil.fileName(list.get(i).getTableName())+"ServiceImpl";
                String serviceName=NameUtil.fileName(list.get(i).getTableName())+"Service";
                String beanName =NameUtil.fileName(list.get(i).getTableName())+"Bean";
                String daoName=NameUtil.fileName(list.get(i).getTableName())+"Dao";
//获得每个表的所有列结构
                List<ColumnStruct> columns =list.get(i).getColumns();
//主键变量名（属性名）
                String columnName =NameUtil.columnName(columns.get(0).getColumnName());
//获得主键数据类型
                String type = columns.get(0).getDataType();
//将mysql数据类型转换为java数据类型
                String dateType =DataTypeUtil.getType(type);

//(ServiceImpl实现类）文件内容
                String packageCon ="package "+serviceImplPackage+";\n\n";
                StringBuffer importCon=new StringBuffer();
                String zhujie="@Service(value = \""+serviceName+"\")\n";
                String className ="public class "+fileName+" implements "+serviceName+"{\n\n";
                StringBuffer classCon = new StringBuffer();

//生成导包内容
                importCon.append("import "+servicePackage+"."+serviceName+";\n\n");
                importCon.append("import"+" "+beanPackage+"."+beanName+";\n\n");
                importCon.append("import"+" "+daoPackage+"."+daoName+";\n\n");
//有date类型的数据需导包
                if("Date".equals(dateType)){
                    importCon.append("import java.util.Date;\n\n");
                }
//有Timestamp类型的数据需导包
                if("Timestamp".equals(dateType)){
                    importCon.append("import java.sql.Timestamp;\n\n");
                }
                importCon.append("import java.util.List;\n\n");

//生成Dao属性
                classCon.append("\t@Autowired"+"\n");
                classCon.append("\tprivate "+daoName+" "+ daoName+";\n\n");
//                classCon.append("\tpublic "+daoName+" get"+ daoName+"(){\n\t\treturn\t"+daoName+";\n\t}\n\n");
//                classCon.append("\tpublic "+daoName+" set"+ daoName+"("+daoName+" "+daoName+"){\n\t\treturn this."+daoName+"="+daoName+";\n\t}\n\n");

//生成实现方法
                classCon.append("\t//添加一条完整记录\n"+"\tpublic int insertRecord("+beanName+" record){\n\t\treturn "+daoName+".insertRecord(record);\n\t}\n\n");
                classCon.append("\t//添加指定列的数据\n"+"\tpublic int insertSelective("+beanName+" record){\n\t\treturn "+daoName+".insertSelective(record);\n\t}\n\n");
                classCon.append("\t//通过Id(主键)删除一条记录\n"+"\tpublic int deleteById("+dateType+" "+columnName+"){\n\t\treturn "+daoName+".deleteById("+columnName+");\n\t}\n\n");
                classCon.append("\t//按Id(主键)修改指定列的值\n"+"\tpublic int updateByIdSelective("+beanName+" record){\n\t\treturn "+daoName+".updateByIdSelective(record);\n\t}\n\n");
                classCon.append("\t//按Id(主键)修改指定列的值\n"+"\tpublic int updateById("+beanName+" record){\n\t\treturn "+daoName+".updateById(record);\n\t}\n\n");
                classCon.append("\t//计算表中的总记录数\n"+"\tpublic int countRecord(){\n\t\treturn "+daoName+".countRecord();\n\t}\n\n");
                classCon.append("\t//根据条件计算记录条数\n"+"\tpublic int countSelective("+beanName+" record){\n\t\treturn "+daoName+".countSelective(record);\n\t}\n\n");
                classCon.append("\t//获得表中的最大Id\n"+"\tpublic int maxId(){\n\t\treturn "+daoName+".maxId();\n\t}\n\n");
                classCon.append("\t//通过Id(主键)查询一条记录\n"+"\tpublic"+" "+beanName+" "+"selectById("+dateType+" "+columnName+"){\n\t\treturn "+daoName+".selectById("+columnName+");\n\t}\n\n");
                classCon.append("\t//查询所有记录\n"+"\tpublic List selectAll(){\n\t\treturn"+daoName+".selectAll();\n\t}\n\n");

//拼接(ServiceImpl实现类）文件内容
                StringBuffer content=new StringBuffer();
                content.append(packageCon);
                content.append(importCon.toString());
                content.append(zhujie);
                content.append(className);
                content.append(classCon.toString());
                content.append("\n}");
                FileUtil.createFileAtPath(path+"/", fileName+".java", content.toString());
            }
            return true;
        }
        return false;
    }
}
