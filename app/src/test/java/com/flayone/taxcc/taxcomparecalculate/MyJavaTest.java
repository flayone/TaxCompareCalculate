package com.flayone.taxcc.taxcomparecalculate;

import org.junit.Test;

public class MyJavaTest {
    private String s = "122";

    @Test
    public void myTest() {
        int s = 9;
        s |= getI();
        System.out.println("按位或结果==" + s);
    }

    private boolean getB() {
        return "22".equals(s);
    }

    private int getI() {
        return 2;
    }

}
