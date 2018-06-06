package com.alien.testng;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test(groups = {"class-group"}) //设置用例级分组
public class GroupsTest {
    private Calculate calculate;
    private int a;
    private int b;
    private int actual;

    @BeforeMethod
    protected void setUp() {
        calculate = new Calculate();
        a = 10;
        b = 5;
    }

    @Test(groups = {"group1", "group2"}) //①设置测试方法级分组，一个方法可以属于多个分组
    public void addTest() {
        actual = calculate.add(a, b);
        assertEquals(15, actual);
    }

    @Test(groups = {"group1", "group3"})
    public void subTest() {
        actual = calculate.sub(a, b);
        assertEquals(5, actual);
    }

    @Test(groups = {"group1"})
    public void mulTest() {
        actual = calculate.mul(a, b);
        assertEquals(50, actual);
    }

    @Test(groups = {"group3"})
    public void divTest() {
        actual = calculate.div(a, b);
        assertEquals(2, actual);
    }

    @AfterMethod
    protected void tearDown() {
    }
}
