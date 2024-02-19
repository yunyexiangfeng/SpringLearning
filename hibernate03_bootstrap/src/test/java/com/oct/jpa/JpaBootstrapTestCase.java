package com.oct.jpa;

import junit.framework.TestCase;

import javax.persistence.*;

/**
 * @Author: Administrator
 * @CreateDate: 2020/12/28 11:25
 * @Description: default
 */
public class JpaBootstrapTestCase extends TestCase {

    @PersistenceUnit(
            unitName = "CRM"
    )
//    @PersistenceUnit
    EntityManagerFactory entityManagerFactory;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * JPA兼容应用程序引导
     * Example 277. Application bootstrapped EntityManagerFactory
     */
    public void testJpaBootstrapFir(){
        EntityManagerFactory crm = Persistence.createEntityManagerFactory("CRM");
        crm.createEntityManager();
    }

    /**
     * JPA兼容容器引导
     * 这里需要借助spring等容器框架才能进行容器引导测试
     * Example 274. Injecting the default EntityManagerFactory
     *      @PersistenceUnit
     * Example 275. Injecting a specific EntityManagerFactory
     *      @PersistenceUnit(
     *             unitName = "CRM"
     *     )
     */
    public void testJpaBootstrapSec(){
        EntityManager entityManager = entityManagerFactory.createEntityManager();
    }


}
