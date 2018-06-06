package com.alien.testng;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class ExceptionTest {
    private Calculate calculate;
    private int a;
    private int b;
    private int actual;

    @BeforeMethod
    protected void setUp() {
        calculate = new Calculate();
        a = 10;
        b = 0;
    }

    //① 在@Test注解中指定了该测试方法抛出ArithmeticException异常
    @Test(enabled = true, expectedExceptions = ArithmeticException.class)
    public void divTest() {
        actual = calculate.div(a, b);
        assertEquals(2, actual);
    }

    @AfterMethod
    protected void tearDown() {
    }
}
