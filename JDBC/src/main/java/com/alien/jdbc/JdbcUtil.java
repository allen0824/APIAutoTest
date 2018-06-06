package com.alien.jdbc;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public final class JdbcUtil {

    static Logger logger = Logger.getLogger(JdbcUtil.class.getName());// 输出本页面日志 初始化

    static Properties pros = null;  //可以帮助读取和处理资源文件中的信息

    static {    //加载JDBCUtil类的时候调用
        LogConfiguration.initLog("db", "JdbcUtil");
        pros = new Properties();
        try {
            //加载配置文件
            pros.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config/db.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 此方法为获取数据库连接
     *
     * @return
     */

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName(pros.getProperty("mysqlDriver")); // 加载数据库驱动
            if (null == conn) {
                //获取数据库连接
                conn = DriverManager.getConnection(pros.getProperty("mysqlURL"),
                        pros.getProperty("mysqlUser"), pros.getProperty("mysqlPwd")
                );
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Sorry,can't find the Driver!");
        } catch (Exception e) {
            System.out.println("Database connection failed!");
        }
        return conn;
    }

    /**
     * 增删改【Add、Del、Update】
     *
     * @param sql 静态的SQL语句
     * @return
     */
    public static int executeNonQuery(String sql) {
        int result = 0;
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            result = stmt.executeUpdate(sql);
        } catch (SQLException err) {
            err.printStackTrace();
            free(null, stmt, conn);
        } finally {
            free(null, stmt, conn);
        }
        return result;
    }

    /**
     * 增删改【Add、Delete、Update】
     *
     * @param sql 动态的SQL语句
     * @param obj 动态的SQL参数
     * @return
     */
    public static int executeNonQuery(String sql, Object... obj) {
        int result = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < obj.length; i++) {
                pstmt.setObject(i + 1, obj[i]);
            }
            result = pstmt.executeUpdate();
        } catch (SQLException err) {
            err.printStackTrace();
            free(null, pstmt, conn);
        } finally {
            free(null, pstmt, conn);
        }
        return result;
    }

    /**
     * 查【Query】
     *
     * @param sql 静态的SQL语句
     * @return
     */
    public static ResultSet executeQuery(String sql) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
        } catch (SQLException err) {
            err.printStackTrace();
            free(rs, stmt, conn);
        }
        return rs;
    }

    /**
     * 查【Query】
     *
     * @param sql 动态的SQL语句
     * @param obj 动态的SQL参数
     * @return
     */
    public static ResultSet executeQuery(String sql, Object... obj) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < obj.length; i++) {
                pstmt.setObject(i + 1, obj[i]);
            }
            rs = pstmt.executeQuery();
        } catch (SQLException err) {
            err.printStackTrace();
            free(rs, pstmt, conn);
        }
        return rs;
    }

    /**
     * 释放【ResultSet】资源
     *
     * @param rs
     */
    public static void free(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException err) {
            err.printStackTrace();
        }
    }

    /**
     * 释放【Statement】资源
     *
     * @param st
     */
    public static void free(Statement st) {
        try {
            if (st != null) {
                st.close();
            }
        } catch (SQLException err) {
            err.printStackTrace();
        }
    }

    /**
     * 释放【Connection】资源
     *
     * @param conn
     */
    public static void free(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException err) {
            err.printStackTrace();
        }
    }

    /**
     * 释放所有数据资源
     *
     * @param rs
     * @param st
     * @param conn
     */
    public static void free(ResultSet rs, Statement st, Connection conn) {
        free(rs);
        free(st);
        free(conn);
    }

    /**
     * 把一个SQL的查询结果数据存放到json对象中
     *
     * @param sqlStr   sql字符串
     * @return 返回JSON对象
     */
    public static JSONObject sqlToJson(String sqlStr) {
        ResultSet qrs = executeQuery( sqlStr);
        String key = null;
        String value = null;
        int rowNum;
        JSONObject jsonObj = new JSONObject();
        JSONObject arrayJson = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            qrs.last();
            rowNum = qrs.getRow();
            qrs.beforeFirst();
            int columnNum = qrs.getMetaData().getColumnCount();
            while (qrs.next()) {
                if (rowNum == 1) {
                    for (int i = 0; i < columnNum; i++) {
                        key = qrs.getMetaData().getColumnLabel(i + 1);
                        value = qrs.getString(i + 1);
                        jsonObj.put(key, value);
                    }
                } else {
                    for (int j = 0; j < columnNum; j++) {
                        key = qrs.getMetaData().getColumnLabel(j + 1);
                        value = qrs.getString(j + 1);
                        arrayJson.put(key, value);
                    }
                    array.add(arrayJson);
                    jsonObj.put("array", array);

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        free(qrs);
        return jsonObj;
    }
}
