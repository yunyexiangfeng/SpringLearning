package com.oct.chap83;

import com.oct.entity.chap83.Customer;
import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.resource.transaction.spi.TransactionStatus;

/**
 * @Author: Administrator
 * @CreateDate: 2020/12/30 19:17
 * @Description: default
 */
public class TransactionApiJtaTestCase extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Example 385. Using Transaction API in JTA (CMT)
     */
    public void testTransactionApiCmt(){
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                // "jdbc" is the default, but for explicitness(明确的)
                .applySetting( AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY, "jta" )
                .build();

        Metadata metadata = new MetadataSources( serviceRegistry )
                .addAnnotatedClass( Customer.class )
                .getMetadataBuilder()
                .build();

        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder()
                .build();

        // Note: depending on the JtaPlatform used and some optional settings,
        // the underlying transactions here will be controlled through either
        // the JTA TransactionManager or UserTransaction

        Session session = sessionFactory.openSession();
        try {
            // Since we are in CMT, a JTA transaction would
            // already have been started.  This call essentially
            // no-ops
            session.getTransaction().begin();

            Number customerCount = (Number) session.createQuery( "select count(c) from Customer c" ).uniqueResult();
            System.out.println(customerCount.toString());

            // Since we did not start the transaction ( CMT ),
            // we also will not end it.  This call essentially
            // no-ops in terms of transaction handling.
            session.getTransaction().commit();
        }
        catch ( Exception e ) {
            // again, the rollback call here would no-op (aside from
            // marking the underlying CMT transaction for rollback only).
            if ( session.getTransaction().getStatus() == TransactionStatus.ACTIVE
                    || session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK ) {
                session.getTransaction().rollback();
            }
            // handle the underlying error
        }
        finally {
            session.close();
            sessionFactory.close();
        }
    }


    /**
     * Example 386. Using Transaction API in JTA (BMT)
     */
    public void testTransactionApiBmt(){
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                // "jdbc" is the default, but for explicitness
                .applySetting( AvailableSettings.TRANSACTION_COORDINATOR_STRATEGY, "jta" )
                .build();

        Metadata metadata = new MetadataSources( serviceRegistry )
                .addAnnotatedClass( Customer.class )
                .getMetadataBuilder()
                .build();

        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder()
                .build();

        // Note: depending on the JtaPlatform used and some optional settings,
        // the underlying transactions here will be controlled through either
        // the JTA TransactionManager or UserTransaction
        Session session = sessionFactory.openSession();
        try {
            // Assuming a JTA transaction is not already active,
            // this call the TM/UT begin method.  If a JTA
            // transaction is already active, we remember that
            // the Transaction associated with the Session did
            // not "initiate" the JTA transaction and will later
            // nop-op the commit and rollback calls...
            session.getTransaction().begin();

            session.persist( new Customer(  ) );
            Customer customer = (Customer) session.createQuery( "select c from Customer c" ).uniqueResult();

            // calls TM/UT commit method, assuming we are initiator.
            session.getTransaction().commit();
        } catch ( Exception e ) {
            // we may need to rollback depending on
            // where the exception happened
            if ( session.getTransaction().getStatus() == TransactionStatus.ACTIVE
                    || session.getTransaction().getStatus() == TransactionStatus.MARKED_ROLLBACK ) {
                // calls TM/UT commit method, assuming we are initiator;
                // otherwise marks the JTA transaction for rollback only
                session.getTransaction().rollback();
            }
            // handle the underlying error
        }
        finally {
            session.close();
            sessionFactory.close();
        }
    }
}
