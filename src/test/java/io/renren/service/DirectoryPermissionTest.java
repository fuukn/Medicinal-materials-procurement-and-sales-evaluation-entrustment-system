package io.renren.service;

import org.junit.jupiter.api.Test;

import java.io.File;
public class DirectoryPermissionTest {
    public static void main(String[] args) {
        String path = "F:/gitProject/renren/renren-fast/src/main/resources/static/uploads/794e18ac-d438-4a98-9d83-2b7b62555023.png";
        File directory = new File(path);

        if (directory.exists() && directory.canRead()) {
            System.out.println("目录可访问！");
        } else {
            System.out.println("目录不可访问！");
        }
    }
}