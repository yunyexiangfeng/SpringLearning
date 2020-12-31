package com.oct.chap53;

import com.oct.entity.chap53.Person;
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
     * Example 302. Making an entity persistent with Hibernate API
     * Example 304. Deleting an entity with the Hibernate API
     */
    public void testNativePersistence(){
        //native bootstrap
        ServiceRegistry registry = new StandardServiceRegistryBuilder().
                configure("hibernate.cfg.xml").
                build();
        Metadata metadata = new MetadataSources(registry).buildMetadata();
        SessionFactory sessionFactory = metadata.buildSessionFactory();

        Session session = sessionFactory.openSession();
        Person person = new Person();
        person.setId(1L);
        person.setName("John Doe");
        session.save(person);

        //delete
        session.delete(person);
    }

    /**
     * Example 301. Making an entity persistent with JPA
     * Example 303. Deleting an entity with JPA
     */
    public void testJpaPersistence(){
        //jpa bootstrap
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("CRM53");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Person person = new Person();
        person.setId(1L);
        person.setName("John Doe");
        entityManager.persist(person);

        //delete
        entityManager.remove(person);
    }
}
