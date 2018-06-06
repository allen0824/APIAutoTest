package com.alien.testng;

import org.testng.annotations.*;

import static org.testng.Assert.*;

public class CalcTest {
    private Calculate calculate;
    private int a;
    private int b;

    @BeforeMethod
    protected void setUp() {
        calculate = new Calculate();
        a = 10;
        b = 5;
    }

    @Test  //① 测试方法，在测试方法打上@Test标签，方法签名可以任意取名
    public void addTest() {
        int actual = calculate.add(a, b);
        //② 可以设定若干个断言方法，这些断言是评判被测试功能是否正确的依据
        assertEquals(15, actual);
    }

    @AfterMethod
    protected void tearDown() {
    }
}
