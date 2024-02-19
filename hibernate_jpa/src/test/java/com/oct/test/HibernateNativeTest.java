package com.oct.test;

import com.oct.entity.annotation.User;
import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;

public class HibernateNativeTest extends TestCase {
    private SessionFactory sessionFactory;

    @Override
    protected void setUp() throws Exception {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml，默认使用classpath下的配置文件
                .build();
        try {
            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            System.out.println(e);
            StandardServiceRegistryBuilder.destroy( registry );
        }
    }

    @Override
    protected void tearDown() throws Exception {
        if ( sessionFactory != null ) {
            sessionFactory.close();
        }
    }

    public void testBasicUsage() {
        // create a couple of events...
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        User user1 = new User();
        user1.setUsername("Tom");
        user1.setAge(22);

        User user2 = new User();
        user2.setUsername("Jack");
        user2.setAge(23);
        session.save(user1);
        session.save(user2);
        session.getTransaction().commit();
        session.close();

        // now lets pull events from the database and list them
        session = sessionFactory.openSession();
        session.beginTransaction();
//        List result = session.createNativeQuery("select * from user").list();
        List<User> result = session.createQuery("from User").list();
        for (User user :  result) {
            System.out.println(user.toString());
        }
        session.getTransaction().commit();
        session.close();
    }
}
