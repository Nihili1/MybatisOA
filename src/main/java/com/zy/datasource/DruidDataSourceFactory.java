package com.zy.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSourceFactory;
import javax.sql.DataSource;
import java.sql.SQLException;

public class DruidDataSourceFactory extends UnpooledDataSourceFactory {


    public DruidDataSourceFactory(){
          this.dataSource=new DruidDataSource();
    }

    @Override
    public DataSource getDataSource() {
        try {
         ((DruidDataSource)this.dataSource).init();   //特殊点，需要初始化Druid 数据源
        } catch (SQLException e) {
            throw new RuntimeException();
        }

         return this.dataSource;
    }
}
