package com.sf.bean;

import java.util.ArrayList;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            User user = new User();
            user.list = new ArrayList<>();
            user.list.addAll(Arrays.asList("1","2","3"));
        }, "t1");

        Thread t2 = new Thread(() -> {
            try {
                t1.join();
                User user = new User();
                user.list.clear();
                user.list.add("4");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, "t2");

        t1.start();
        t2.start();
        new User().list.forEach(System.out::println);
    }
}
