package com.test.auto.daoImpl;

import com.test.auto.bean.ColumnStruct;
import com.test.auto.bean.TableStruct;
import com.test.auto.dao.GetTablesDao;
import com.test.auto.dao.ServiceAutoDao;
import com.test.auto.util.ConfigUtil;
import com.test.auto.util.DataTypeUtil;
import com.test.auto.util.FileUtil;
import com.test.auto.util.NameUtil;

import java.util.List;

public class ServiceAutoDaoImpl implements ServiceAutoDao {
    //从GetTablesDaoImpl中获得装有所有表结构的List
    GetTablesDao getTables = new GetTablesDaoImpl();
    List<TableStruct> list = getTables.getTablesStruct();

    //通过表名、字段名称、字段类型创建Service接口
    public boolean createService() {
//获得配置文件的参数
//项目路径
        String projectPath = ConfigUtil.projectPath;
//是否生成Service
        String serviceFalg=ConfigUtil.serviceFlag;
//Service接口的包名
        String servicePackage=ConfigUtil.servicePackage;
//Bean实体类的包名
        String beanPackage=ConfigUtil.beanPackage;
        if("true".equals(serviceFalg) ){
//将包名com.xxx.xxx形式，替换成com/xxx/xxx形成
            String servicePath=servicePackage.replace(".", "/");
//Service接口的路径
            String path =projectPath+"/src/"+servicePath;
//遍历装有所有表结构的List
            for (int i = 0; i < list.size(); i++) {
//文件名
                String fileName=NameUtil.fileName(list.get(i).getTableName())+"Service";
                String beanName =NameUtil.fileName(list.get(i).getTableName())+"Bean";
//获得每个表的所有列结构
                List<ColumnStruct> columns =list.get(i).getColumns();
//主键变量名（属性名）
                String columnName =NameUtil.columnName(columns.get(0).getColumnName());
//获得主键数据类型
                String type = columns.get(0).getDataType();
//将mysql数据类型转换为java数据类型
                String dateType =DataTypeUtil.getType(type);

//(Service接口）文件内容
                String packageCon ="package "+servicePackage+";\n\n";
                StringBuffer importCon=new StringBuffer();
                String className ="public interface "+fileName+"{\n\n";
                StringBuffer classCon = new StringBuffer();

//生成导包内容
                importCon.append("import"+" "+beanPackage+"."+beanName+";\n\n");
//有date类型的数据需导包
                if("Date".equals(dateType)){
                    importCon.append("import java.util.Date;\n\n");
                }
//有Timestamp类型的数据需导包
                if("Timestamp".equals(dateType)){
                    importCon.append("import java.sql.Timestamp;\n\n");
                }
                importCon.append("import java.util.List;\n\n");

//生成接口方法
                classCon.append("\t"+"public int insertRecord("+beanName+" record);//添加一条完整记录\n\n");
                classCon.append("\t"+"public int insertSelective("+beanName+" record);//添加指定列的数据\n\n");
                classCon.append("\t"+"public int deleteById("+dateType+" "+columnName+");//通过Id(主键)删除一条记录\n\n");
                classCon.append("\t"+"public int updateByIdSelective("+beanName+" record);//按Id(主键)修改指定列的值\n\n");
                classCon.append("\t"+"public int updateById("+beanName+" record);//按Id(主键)修改指定列的值\n\n");
                classCon.append("\t"+"public int countRecord();//计算表中的总记录数\n\n");
                classCon.append("\t"+"public int countSelective("+beanName+" record);//根据条件计算记录条数\n\n");
                classCon.append("\t"+"public int maxId();//获得表中的最大Id\n\n");
                classCon.append("\t"+"public"+" "+beanName+" "+"selectById("+dateType+" "+columnName+");//通过Id(主键)查询一条记录\n\n");
                classCon.append("\t"+"public List selectAll();//查询所有记录\n\n");

//拼接(Service接口）文件内容
                StringBuffer content=new StringBuffer();
                content.append(packageCon);
                content.append(importCon.toString());
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
