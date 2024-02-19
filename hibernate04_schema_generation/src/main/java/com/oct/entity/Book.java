package com.oct.entity;

import org.hibernate.annotations.Check;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * @Author: Administrator
 * @CreateDate: 2020/12/27 17:00
 * @Description: default
 */
@Entity(name = "Book")
@Check(constraints = "CASE WHEN isbn IS NOT NULL THEN LENGTH(isbn) = 13 ELSE true END")
public class Book {

    @Id
    private Long id;

    private String title;

    @NaturalId
    private String isbn;

    @ManyToOne
    private Person author;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }
}
