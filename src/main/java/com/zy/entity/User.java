package com.zy.entity;

public class User {
    private Long userId;
    private String name;
    private String password;
    private Long empId;
    private Integer cipher;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getEmpId() {
        return empId;
    }

    public void setEmpId(Long empId) {
        this.empId = empId;
    }

    public Integer getCipher() {
        return cipher;
    }

    public void setCipher(Integer cipher) {
        this.cipher = cipher;
    }
}
