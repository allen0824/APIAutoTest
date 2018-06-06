package com.alien.emmatesting.base;

import com.alien.emmatesting.util.ExcelDataProvider;
import com.alien.emmatesting.util.LogConfiguration;
import org.apache.log4j.Logger;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.Iterator;

public class BaseParpare {

    // 输出本页面日志 初始化
    static Logger logger = Logger.getLogger(BaseParpare.class.getName());
    public static String moduleName = null; // 模块的名字
    public static String functionName = null; // 功能的名字
    public static String caseNum = null; // 用例编号

    @BeforeTest()
    public void beforeTest() {
        LogConfiguration.initLog(getModuleName(), this.getClass()
                .getSimpleName());
        logger.info("====================================接口测试开始====================================");
    }

    @BeforeClass
    /**接口测试前准备工作*/
    public void startTest() {
        moduleName = getModuleName();
        functionName = getFunctionName();
        caseNum = getCaseNum();
        logger.info(moduleName + "模块" + functionName + "接口" + caseNum
                + "用例测试开始");

    }

    @AfterClass
    /**接口测试完成后需要做的相关工作*/
    public void endTest() {
        logger.info(moduleName + "模块" + functionName + "接口" + caseNum
                + "用例测试完毕");
    }

    @AfterTest
    /**接口测试完成后需要做的相关工作*/
    public void AfterTest() {
        logger.info("====================================接口测试完毕====================================");
    }

    /**
     * 测试数据提供者 - 方法
     */
    @DataProvider(name = "testData")
    public Iterator<Object[]> dataFortestMethod() throws IOException {
        // 将模块名称和用例的编号传给 ExcelDataProvider ，然后进行读取excel数据
        return new ExcelDataProvider(moduleName + "/" + functionName, caseNum);//
    }

    /**
     * 获取模块名
     *
     * @return
     */
    public String getModuleName() {
        String className = this.getClass().getName();
        String moduleName, startStr;
        int lastDotIndexNum, secondLastDotIndexNum;
        startStr = "testcase.";
        lastDotIndexNum = className.lastIndexOf("."); // 取得最后一个.的index
        secondLastDotIndexNum = className.indexOf(startStr) + startStr.length();
        moduleName = className
                .substring(secondLastDotIndexNum, lastDotIndexNum); // 取到模块的名称
        return moduleName;
    }

    /**
     * 获取功能名
     *
     * @return
     */
    public String getFunctionName() {
        String className = this.getClass().getName();
        int underlineIndexNum = className.indexOf("_"); // 取得第一个_的index
        if (underlineIndexNum > 0) {
            functionName = className.substring(className.lastIndexOf(".") + 1,
                    underlineIndexNum); // 取到模块的名称
        }
        return functionName;
    }

    /**
     * 获取用例编号
     */
    public String getCaseNum() {
        String className = this.getClass().getName();
        int underlineIndexNum = className.indexOf("_"); // 取得第一个_的index
        if (underlineIndexNum > 0) {
            caseNum = className.substring(underlineIndexNum + 1,
                    underlineIndexNum + 4); // 取到用例编号
        }
        return caseNum;
    }
}
