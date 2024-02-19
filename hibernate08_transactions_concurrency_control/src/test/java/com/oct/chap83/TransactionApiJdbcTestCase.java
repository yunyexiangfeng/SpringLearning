package com.oct.chap83;

import com.oct.entity.chap83.Customer;
import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import java.util.List;

/**
 * @Author: Administrator
 * @CreateDate: 2020/12/30 18:53
 * @Description: default
 */
public class TransactionApiJdbcTestCase extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Example 384. Using Transaction API in JDBC
     */
    public void testTransactionApiJdbc(){
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                // "jdbc" is the default, but for explicitness
                .applySetting(AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY, "jdbc")
                .build();

        Metadata metadata = new MetadataSources(registry).
                addAnnotatedClass(Customer.class).
                getMetadataBuilder().
                build();

        SessionFactory sessionFactory = metadata.
                getSessionFactoryBuilder().
                build();

        Session session = sessionFactory.openSession();

        try {
            // calls Connection#setAutoCommit( false ) to
            // signal start of transaction
            session.getTransaction().begin();
            session.createQuery("update Customer a set a.name = 'dd'").executeUpdate();
            // calls Connection#commit(), if an error
            // happens we attempt a rollback
            session.getTransaction().commit();
        } catch (Exception e){
            // we may need to rollback depending on
            // where the exception happened
            if ( session.getTransaction().getStatus() == TransactionStatus.ACTIVE
                    || session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK ) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
            sessionFactory.close();
        }
    }
}
