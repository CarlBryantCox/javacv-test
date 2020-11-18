package com.chw.test;

public class Test {

    public static void main(String[] args) {
        String path = Test.class.getClassLoader().getResource("pic/test_01.png").getPath();
        System.out.println(path.substring(1));
    }
}
