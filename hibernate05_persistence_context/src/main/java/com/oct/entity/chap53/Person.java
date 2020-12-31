package com.oct.entity.chap53;

import com.oct.entity.chap52.Book;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Administrator
 * @CreateDate: 2020/12/28 17:20
 * @Description: default
 */
@Entity(name = "Person")
public class Person {
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
}
