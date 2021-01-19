package com.oct.entity.chap83;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @Author: Administrator
 * @CreateDate: 2020/12/30 18:59
 * @Description: default
 */
@Entity(name = "Customer")
public class Customer {

    @Id
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
