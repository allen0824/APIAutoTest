package com.alien.emmatesting.util;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.log4j.Logger;
import org.testng.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @description: 读取Excel数据<br>
 * 说明：<br>
 * Excel放在Data文件夹下<br>
 * Excel命名方式：测试类名.xls<br>
 * Excel的sheet命名方式：测试方法名<br>
 * Excel第一行为Map键值<br>
 */
public class ExcelDataProvider implements Iterator<Object[]> {

    private Workbook book = null;
    private Sheet sheet = null;
    private int rowNum = 0;
    private int currentRowNo = 0;
    private int columnNum = 0;
    private String[] columnnName;
    private String path = null;
    private InputStream inputStream = null;
    public static Logger logger = Logger.getLogger(ExcelDataProvider.class
            .getName());

    /**
     * 数据驱动方法
     *
     * @param moduleName 模块的名称
     * @param caseNum    测试用例编号，即sheet页
     */
    public ExcelDataProvider(String moduleName, String caseNum) {
        try {
            // 文件路径
            //直接使用testng运行时，路径为："src/main/resources/data/" + moduleName + ".xls"
            path = "classes/data/" + moduleName + ".xls";//使用maven运行时路径
            inputStream = new FileInputStream(path);
            book = Workbook.getWorkbook(inputStream);
            sheet = book.getSheet(caseNum); // 读取sheet页
            rowNum = sheet.getRows(); // 获得该sheet的所有行
            Cell[] cell = sheet.getRow(0);// 获得第一行的所有单元格
            columnNum = cell.length; // 单元格的个数，值赋给列数
            columnnName = new String[columnNum];// 开辟列名的大小
            for (int i = 0; i < columnNum; i++) {// 一行的值
                columnnName[i] = cell[i].getContents().toString();// 被赋予为列名
            }
            this.currentRowNo++;
        } catch (FileNotFoundException e) {
            logger.error("没有找到指定的文件：" + "[" + path + "]");
            Assert.fail("没有找到指定的文件：" + "[" + path + "]");
        } catch (Exception e) {
            logger.error("不能读取文件： [" + path + "]", e);
            Assert.fail("不能读取文件： [" + path + "]");
        }
    }

    /**
     * 判断excel文件sheet页中是否还有下一个内容
     *
     * @return 返回true或false
     */
    @Override
    public boolean hasNext() {
        if (this.rowNum == 0 || this.currentRowNo >= this.rowNum) {
            try {
                inputStream.close();
                book.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        } else {
            // sheet下一行内容为空判定结束
            if ((sheet.getRow(currentRowNo))[0].getContents().equals(""))
                return false;
            return true;
        }
    }


    /**
     * 以object形式返回行中的内容
     *
     * @return
     */
    @Override
    public Object[] next() {
        Cell[] c = sheet.getRow(this.currentRowNo);
        Map<String, String> data = new HashMap<String, String>();
        for (int i = 0; i < this.columnNum; i++) {
            String temp = "";
            try {
                temp = c[i].getContents().toString();
            } catch (ArrayIndexOutOfBoundsException ex) {
                temp = "";
            }
            data.put(this.columnnName[i], temp);
        }
        Object object[] = new Object[1];
        object[0] = data;
        this.currentRowNo++;
        return object;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove unsupported.");
    }
}
