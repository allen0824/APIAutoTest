package com.alien.testng;


import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class DataProviderTest {
    private Calculate calculate;
    private int a;
    private int b;

    @BeforeMethod
    protected void setUp() {
        calculate = new Calculate();
    }

    @DataProvider(name = "TestData") //参数数据提供者
    public Object[][] testData() {
        return new Object[][]{
                {0, 0, 0},
                {1, 100, 101},
                {0, 100, 100}
        };
    }

    @Test(dataProvider = "TestData")  //① @Test属性dataProvider属性值要与参数数据提供者的dataProvider值保持一致
    public void addTest(int i, int j, int expect) {
        int actual = calculate.add(i, j);
        assertEquals(expect, actual);
    }

    @AfterMethod
    protected void tearDown() {
    }
}
