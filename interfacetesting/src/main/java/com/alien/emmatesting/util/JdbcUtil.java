package com.alien.emmatesting.util;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.sql.*;

public final class JdbcUtil {

    static Logger logger = Logger.getLogger(JdbcUtil.class.getName());// 输出本页面日志 初始化

    /**
     * 此方法为获取数据库连接
     *
     * @return
     */
    public static Connection getConnection() {
        Connection conn = null;
        //直接使用testng运行时，路径为："src/main/resources/config/db.properties";
        String dbConfPath = "classes/config/db.properties";//使用maven运行时路径
        try {
            String driver = PropertiesDataProvider.getTestData(dbConfPath, "mysqlDriver"); // 数据库驱动
            String url = PropertiesDataProvider.getTestData(dbConfPath, "mysqlURL");// 数据库
            String user = PropertiesDataProvider.getTestData(dbConfPath, "mysqlUser"); // 用户名
            String password = PropertiesDataProvider.getTestData(dbConfPath, "mysqlPwd"); // 密码
            Class.forName(driver); // 加载数据库驱动
            if (null == conn) {
                conn = DriverManager.getConnection(url, user, password);
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
     * @param sql
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
     * @param sql
     * @param obj
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
     * @param sql
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
     * @param sql
     * @param obj
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
     * 把一个SQL的查询结果数据存放到json对象中
     *
     * @param sqlStr sql字符串
     * @return 返回JSON对象
     */
    public static JSONObject sqlToJson(String sqlStr) {
        ResultSet qrs = executeQuery(sqlStr);
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

    /**
     * 判断记录是否存在
     *
     * @param database
     * @param sql
     * @return
     */
    public static Boolean isExist(String database, String sql) {
        ResultSet rs = null;
        try {
            rs = executeQuery(database, sql);
            rs.last();
            int count = rs.getRow();
            if (count > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException err) {
            err.printStackTrace();
            free(rs);
            return false;
        } finally {
            free(rs);
        }
    }

    /**
     * 判断记录是否存在
     *
     * @param database
     * @param sql
     * @param obj
     * @return Boolean
     */
    public static Boolean isExist(String database, String sql, Object... obj) {
        ResultSet rs = null;
        try {
            rs = executeQuery(database, sql, obj);
            rs.last();
            int count = rs.getRow();
            if (count > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException err) {
            err.printStackTrace();
            free(rs);
            return false;
        } finally {
            free(rs);
        }
    }

    /**
     * 获取查询记录的总行数
     *
     * @param database
     * @param sql
     * @return int
     */
    public static int getCount(String database, String sql) {
        int result = 0;
        ResultSet rs = null;
        try {
            rs = executeQuery(database, sql);
            rs.last();
            result = rs.getRow();
        } catch (SQLException err) {
            free(rs);
            err.printStackTrace();
        } finally {
            free(rs);
        }
        return result;
    }

    /**
     * 获取查询记录的总行数
     *
     * @param database
     * @param sql
     * @param obj
     * @return int
     */
    public static int getCount(String database, String sql, Object... obj) {
        int result = 0;
        ResultSet rs = null;
        try {
            rs = executeQuery(database, sql, obj);
            rs.last();
            result = rs.getRow();
        } catch (SQLException err) {
            err.printStackTrace();
        } finally {
            free(rs);
        }
        return result;
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
}
