package com.alien.testng;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class DependTest {
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

    @Test(dependsOnMethods = {"subTest", "mulTest"},alwaysRun=true) //①设置在当前测试方法执行之前先被调用的测试方法
    public void addTest() {
        actual = calculate.add(a, b);
        assertEquals(15, actual);
        System.out.println("我运行的是add方法!");
    }

    @Test(dependsOnGroups = {"group2"}, priority = 0)//②设置在当前测试方法执行之前先被调用group2组的测试方法
    public void subTest() {
        actual = calculate.sub(a, b);
        assertEquals(5, actual);
        System.out.println("我运行的是sub方法!");
    }

    @Test(groups = {"group1"}, priority = 1)
    public void mulTest() {
        actual = calculate.mul(a, b);
        assertEquals(50, actual);
        System.out.println("我运行的是mul方法!");
    }

    @Test(groups = {"group2"})
    public void divTest() {
        actual = calculate.div(a, b);
        assertEquals(2, actual);
        System.out.println("我运行的是div方法!");
    }

    @AfterMethod
    protected void tearDown() {
    }
}
