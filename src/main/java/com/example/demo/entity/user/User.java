package com.example.demo.entity.user;

import com.example.demo.entity.BaseEntity;

public class User extends BaseEntity {
    private String name;
    private int age;
    private int sex;
    private String address;
    private String phone;
    private String email;
    private String password;
    private String salt;
    private String token;
    private String remark;
    private String status;
    public User() {
        super();
    }
}
