package com.alien.testng;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class MultipleMethodTest {
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

    @Test(priority = 0)  //① 在@Test注解中指定了该测试方法的运行优先级为0，最高优先级
    public void addTest() {
        actual = calculate.add(a, b);
        assertEquals(15, actual);
        System.out.println("我运行的是add方法!");
    }

    @Test(priority = 1)//② 在@Test注解中指定了该测试方法的运行优先级为1
    public void subTest() {
        actual = calculate.sub(a, b);
        assertEquals(5, actual);
        System.out.println("我运行的是sub方法!");
    }

    @Test(priority = 2)//③ 在@Test注解中指定了该测试方法的运行优先级为2
    public void mulTest() {
        actual = calculate.mul(a, b);
        assertEquals(50, actual);
        System.out.println("我运行的是mul方法!");
    }

    @Test(priority = 3)//④ 在@Test注解中指定了该测试方法的运行优先级为3
    public void divTest() {
        actual = calculate.div(a, b);
        assertEquals(2, actual);
        System.out.println("我运行的是div方法!");
    }

    @AfterMethod
    protected void tearDown() {
    }
}
