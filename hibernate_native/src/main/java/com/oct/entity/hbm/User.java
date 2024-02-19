package com.oct.entity.hbm;

public class User {

    public Integer id;
    public String username;
    private Integer age;

    public User() {
        //default constructor for hibernate
    }

    /**
     * For application
     * @param username
     * @param age
     */
    public User(String username, Integer age) {
        this.username = username;
        this.age = age;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", age=" + age +
                '}';
    }
}
