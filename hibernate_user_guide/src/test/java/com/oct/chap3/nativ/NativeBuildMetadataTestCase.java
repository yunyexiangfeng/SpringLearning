package com.oct.chap3.nativ;

import com.oct.chap3.entity.MyEntity;
import junit.framework.TestCase;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

/**
 * @Author: Administrator
 * @CreateDate: 2020/12/27 15:46
 * @Description: default
 */
public class NativeBuildMetadataTestCase extends TestCase {

    @Override
    protected void setUp() throws Exception {
        exampleFir();
        exampleSec();
    }

    /**
     * Example 270. Configuring a MetadataSources with method chaining
     */
    private void exampleFir(){
        ServiceRegistry standardRegistry =
                new StandardServiceRegistryBuilder().build();

        MetadataSources sources = new MetadataSources( standardRegistry )
                .addAnnotatedClass( MyEntity.class )
                .addAnnotatedClassName( "com.oct.chap3.entity.Customer" )
                .addResource( "chap3/Order.hbm.xml" )
                .addResource( "chap3/Product.orm.xml" );
        Metadata metadata = sources.buildMetadata();
    }

    /**
     * Example 271. Building Metadata via MetadataBuilder
     */
    private void exampleSec(){

        ServiceRegistry registry = new StandardServiceRegistryBuilder().build();

        MetadataSources metadataSources = new MetadataSources(registry);

        MetadataBuilder metadataBuilder = metadataSources.getMetadataBuilder();

        // Use the JPA-compliant implicit naming strategy
        metadataBuilder.applyImplicitNamingStrategy(ImplicitNamingStrategyJpaCompliantImpl.INSTANCE);

        // specify the schema name to use for tables, etc when none is explicitly specified
        metadataBuilder.applyImplicitSchemaName( "my_default_schema" );

        // specify a custom Attribute Converter
//        metadataBuilder.applyAttributeConverter( myAttributeConverter );

        Metadata metadata = metadataBuilder.build();

    }
}
