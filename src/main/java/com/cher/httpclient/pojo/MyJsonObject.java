package com.cher.httpclient.pojo;

import java.util.List;

public class MyJsonObject {

    private String username;
    private String password;
    private Integer age;
    private List<String> hobbies;

    public MyJsonObject() {
    }

    public MyJsonObject(String username, String password, Integer age, List<String> hobbies) {
        this.username = username;
        this.password = password;
        this.age = age;
        this.hobbies = hobbies;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public List<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }

    @Override
    public String toString() {
        return "MyJsonObject{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                ", hobbies=" + hobbies +
                '}';
    }
}
