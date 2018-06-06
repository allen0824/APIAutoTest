package com.alien.testng;


import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class TimeoutTest {
    private Calculate calculate;
    private int a;
    private int b;

    @BeforeMethod
    protected void setUp() {
        calculate = new Calculate();
        a = 10;
        b = 5;
    }

    @Test(timeOut = 3000)  //① 测试在指定时间内运行完即正常
    public void addTest() {
        int actual = calculate.add(a, b);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(15, actual);
    }

    @AfterMethod
    protected void tearDown() {
    }

}
