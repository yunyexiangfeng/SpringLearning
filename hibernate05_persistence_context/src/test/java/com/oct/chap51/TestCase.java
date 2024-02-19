package com.oct.chap51;

import com.oct.entity.chap52.Book;
import com.oct.entity.chap52.Person;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @Author: Administrator
 * @CreateDate: 2020/12/28 16:56
 * @Description: default
 */
public class TestCase extends junit.framework.TestCase {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Example 294. Accessing Hibernate APIs from JPA
     */
    public void testFir(){
        Session session = entityManager.unwrap(Session.class);
        SessionImplementor sessionImplementor = entityManager.unwrap(SessionImplementor.class);
        entityManager.getEntityManagerFactory().unwrap(SessionFactory.class);
    }


    /**
     * Example 296. Bidirectional association
     * Example 297. Incorrect normal Java usage
     */
    public void testSec(){
        Person person = new Person();
        person.setName("John Doe");

        Book book = new Book();
        person.getBooks().add(book);
        try {
            book.getAuthor().getName();
        }
        catch (NullPointerException expected) {
            // This blows up ( NPE ) in normal Java usage
            expected.printStackTrace();
        }
    }

    /**
     * Example 298. Correct normal Java usage
     */
    public void testThi(){
        Person person = new Person();
        person.setName( "John Doe" );

        Book book = new Book();
        person.getBooks().add( book );
        book.setAuthor( person );

        System.out.println(book.getAuthor().getName());
    }
}
