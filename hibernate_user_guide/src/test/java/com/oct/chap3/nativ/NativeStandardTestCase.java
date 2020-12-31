package com.oct.chap3.nativ;

import com.oct.chap3.entity.MyEntity;
import junit.framework.TestCase;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import java.io.File;

/**
 * @Author: Administrator
 * @CreateDate: 2020/12/27 14:58
 * @Description: default
 */
public class NativeStandardTestCase extends TestCase {

    @Override
    protected void setUp() throws Exception {
        exampleFir();
        exampleSec();
    }

    /**
     *Example 267. Building a BootstrapServiceRegistryBuilder
     */
    private void exampleFir(){
        // An example using an implicitly built BootstrapServiceRegistry
        StandardServiceRegistryBuilder standardRegistryBuilder =
                new StandardServiceRegistryBuilder();
        // An example using an explicitly built BootstrapServiceRegistry
        BootstrapServiceRegistry bootstrapRegistry =
                new BootstrapServiceRegistryBuilder().build();
//        StandardServiceRegistryBuilder standardRegistryBuilder =
//                new StandardServiceRegistryBuilder( bootstrapRegistry );
    }

    /**
     * Example 268. Configuring a MetadataSources
     */
    private void exampleSec(){

        ServiceRegistry registry = new StandardServiceRegistryBuilder().build();

        MetadataSources sources = new MetadataSources(registry);
        // alternatively, we can build the MetadataSources without passing
        // a service registry, in which case it will build a default
        // BootstrapServiceRegistry to use.  But the approach shown
        // above is preferred
        // MetadataSources sources = new MetadataSources();

        // add a class using JPA/Hibernate annotations for mapping
        sources.addAnnotatedClass( MyEntity.class );

        // add the name of a class using JPA/Hibernate annotations for mapping.
        // differs from above in that accessing the Class is deferred which is
        // important if using runtime bytecode-enhancement
        sources.addAnnotatedClassName("com.oct.chap3.entity.Customer");

        // Read package-level metadata.
        sources.addPackage("com.oct");

        // Read package-level metadata.
        sources.addPackage(MyEntity.class.getPackage());

        // Adds the named hbm.xml resource as a source: which performs the
        // classpath lookup and parses the XML
        sources.addResource("chap3/Order.hbm.xml");

        // Adds the named JPA orm.xml resource as a source: which performs the
        // classpath lookup and parses the XML
        sources.addResource( "chap3/Product.orm.xml" );

        // Read all mapping documents from a directory tree.
        // Assumes that any file named *.hbm.xml is a mapping document.
        sources.addDirectory(new File("."));

        // Read mappings from a particular XML file
        sources.addJar(new File("./entities.jar"));

        // Read a mapping as an application resource using the convention that a class named foo.bar.MyEntity is
        // mapped by a file named foo/bar/MyEntity.hbm.xml which can be resolved as a classpath resource.
        sources.addClass(MyEntity.class);
    }
}
