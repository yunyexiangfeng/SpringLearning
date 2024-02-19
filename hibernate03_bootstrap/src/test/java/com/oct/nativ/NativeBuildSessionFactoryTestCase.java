package com.oct.nativ;

import com.oct.entity.MyEntity;
import junit.framework.TestCase;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * @Author: Administrator
 * @CreateDate: 2020/12/27 16:16
 * @Description: default
 */
public class NativeBuildSessionFactoryTestCase extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        exampleFir();
        exampleSec();
    }

    /**
     * Example 272. Native Bootstrapping - Putting it all together
     */
    private void exampleFir(){
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml")
                .build();

        Metadata metadata = new MetadataSources()
                .addAnnotatedClass( MyEntity.class )
                .addAnnotatedClassName( "org.hibernate.example.Customer" )
                .addResource( "org/hibernate/example/Order.hbm.xml" )
                .addResource( "org/hibernate/example/Product.orm.xml" )
                .getMetadataBuilder()
                .applyImplicitNamingStrategy( ImplicitNamingStrategyJpaCompliantImpl.INSTANCE )
                .build();

        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder()
//                .applyBeanManager( getBeanManager() )
                .build();
    }

    /**
     * Example 273. Building SessionFactory via SessionFactoryBuilder
     */
    private void exampleSec(){

        StandardServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                .configure( "org/hibernate/example/hibernate.cfg.xml" )
                .build();

        Metadata metadata = new MetadataSources( standardRegistry )
                .addAnnotatedClass( MyEntity.class )
                .addAnnotatedClassName( "org.hibernate.example.Customer" )
                .addResource( "org/hibernate/example/Order.hbm.xml" )
                .addResource( "org/hibernate/example/Product.orm.xml" )
                .getMetadataBuilder()
                .applyImplicitNamingStrategy( ImplicitNamingStrategyJpaCompliantImpl.INSTANCE )
                .build();

        SessionFactoryBuilder sessionFactoryBuilder = metadata.getSessionFactoryBuilder();

        // Supply a SessionFactory-level Interceptor
//        sessionFactoryBuilder.applyInterceptor( new CustomSessionFactoryInterceptor() );

        // Add a custom observer
//        sessionFactoryBuilder.addSessionFactoryObservers( new CustomSessionFactoryObserver() );

        // Apply a CDI BeanManager ( for JPA event listeners )
//        sessionFactoryBuilder.applyBeanManager( getBeanManager() );

        SessionFactory sessionFactory = sessionFactoryBuilder.build();
    }
}
