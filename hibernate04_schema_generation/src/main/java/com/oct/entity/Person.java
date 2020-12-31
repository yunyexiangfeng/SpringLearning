package com.oct.entity;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Administrator
 * @CreateDate: 2020/12/27 16:58
 * @Description: default
 */
@Entity(name = "Person")
public class Person {

    @Id
    private Long id;

    @ColumnDefault("'N/A'")
    private String name;

    @OneToMany(mappedBy = "author")
    private List<Book> books = new ArrayList();

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

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}
