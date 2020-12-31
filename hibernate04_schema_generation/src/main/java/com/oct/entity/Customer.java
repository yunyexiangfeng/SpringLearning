package com.oct.entity;

import org.hibernate.annotations.LazyGroup;

import javax.persistence.*;
import java.sql.Blob;
import java.util.UUID;

/**
 * @Author: Administrator
 * @CreateDate: 2020/12/27 16:55
 * @Description: default
 */
@Entity(name = "Customer")
public class Customer {

    @Id
    private Integer id;

    private String name;

    @Basic(fetch = FetchType.LAZY)
    private UUID accountsPayableXrefId;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @LazyGroup("lobs")
    private Blob image;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getAccountsPayableXrefId() {
        return accountsPayableXrefId;
    }

    public void setAccountsPayableXrefId(UUID accountsPayableXrefId) {
        this.accountsPayableXrefId = accountsPayableXrefId;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }
}
