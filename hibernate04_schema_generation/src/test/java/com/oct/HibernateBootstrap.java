package com.oct;

import com.oct.entity.Book;
import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;


/**
 * @Author: Administrator
 * @CreateDate: 2020/12/27 17:36
 * @Description: default
 */
public class HibernateBootstrap extends TestCase {


    private SessionFactory sessionFactory;


    /**
     * Example 282. Schema generation Domain Model
     * Example 285. Enabling schema generation import file
     * Example 286. Schema generation HBM database-object
     */
    @Override
    protected void setUp() throws Exception {
        ServiceRegistry registry = new StandardServiceRegistryBuilder().
                configure("hibernate.cfg.xml").//默认使用hibernate.properties ，所以hibernate.cfg.xml要显示配置
                build();
        Metadata metadata = new MetadataSources(registry).buildMetadata();
        sessionFactory = metadata.buildSessionFactory();
    }

    /**
     * Example 287. Database check entity mapping example
     * Example 288. Database check failure example
     */
    public void testExampleFir(){
        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            Book book = new Book();
            book.setId(1l);
            book.setTitle( "High-Performance Java Persistence" );
            book.setIsbn( "11-11-2016" );
            session.save(book);
            session.getTransaction().commit();
            session.close();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void testExampleSec(){

    }
}
