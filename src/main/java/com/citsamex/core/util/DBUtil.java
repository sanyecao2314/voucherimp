package com.citsamex.core.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * jdbc操作数据库工具类.
 * @author fans.fan
 *
 */
public class DBUtil {

	private static final Logger logger = Logger.getLogger(DBUtil.class);
	static InvDataSource ds = new InvDataSource();
    /**
     * 
     * @param sql sql statement
     * @param location  CAN BJS SHA
     * @return
     */
    public static List querySql(String sql) throws Exception {
        List list = new ArrayList();
        Map<String,Object> row = null;
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        try {
            conn = ds.getConnection();
            stat = conn.createStatement();
            System.out.println("sql excute: " + sql);
            rs = stat.executeQuery(sql);
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            while (rs.next()) {
                row = new HashMap<String,Object>();
                for (int i = 1; i <= cols ; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                list.add(row);
            }
        } finally {
        	close(rs);
        	close(stat);
        	close(conn);
        }
        
        return list;
    }
    
    /**
     * 只有一个返回值的查询
     * @param sql
     * @return
     * @throws Exception
     */
    public static Object querySqlUniqueResult(String sql) throws Exception {
    	Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;
        Object obj = null;
        try {
        	conn = ds.getConnection();
            stat = conn.createStatement();
            rs = stat.executeQuery(sql);
            while (rs.next()) {
            	obj =rs.getObject(1);
            }
        } finally {
        	close(conn);
        	close(stat);
        	close(rs);
        }
        
        return obj;
    }
    
    
    public static void excuteUpdate(String sql) throws Exception {
        Connection conn = null;
        Statement stat = null;
        try {
            conn = ds.getConnection();
            stat = conn.createStatement();
            Logger.getRootLogger().info("sql excute: " + sql);
            stat.executeUpdate(sql);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
           close(stat);
           close(conn);
        }
    }
    
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = ds.getConnection();
        } catch (SQLException e) {
        	logger.error("数据库连接错误:"+e);
        	e.printStackTrace();
        }
        
        return conn;
    }
    
    
    /**
     * 关闭连接
     * @param closeable
     */
    public static void close(AutoCloseable closeable){
    	if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
    
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        List list;
		try {
			list = querySql("select 1");
			int i = 0;
			while (i < list.size()) {
				System.out.println(list.get(0));
				i++;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
