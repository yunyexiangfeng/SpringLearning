package com.oct.chap54;

import com.oct.entity.chap52.Book;
import com.oct.entity.chap52.Person;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @Author: Administrator
 * @CreateDate: 2020/12/28 18:37
 * @Description: default
 */
public class TestCase extends junit.framework.TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }

    /**
     * Example 306. Obtaining an entity reference without initializing its data with Hibernate API
     */
    public void testNativePersistence(){
        //native bootstrap
        ServiceRegistry registry = new StandardServiceRegistryBuilder().
                configure("hibernate.cfg.xml").
                build();
        Metadata metadata = new MetadataSources(registry).buildMetadata();
        SessionFactory sessionFactory = metadata.buildSessionFactory();

        Session session = sessionFactory.openSession();
    }

    /**
     * Example 305. Obtaining an entity reference without initializing its data with JPA
     */
    public void testJpaPersistence(){
        //jpa bootstrap
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("CRM53");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            Book book = new Book();
            Person reference = entityManager.getReference(Person.class, 1L);
            book.setAuthor(reference);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
