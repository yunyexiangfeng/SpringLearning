package com.oct.chap3.nativ;

import junit.framework.TestCase;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;

/**
 * @Author: Administrator
 * @CreateDate: 2020/12/27 14:49
 * @Description: default
 */
public class NativeBootstrapTestCase extends TestCase {

    @Override
    protected void setUp() throws Exception {
        exampleFir();
    }

    /**
     * Example 266. Controlling BootstrapServiceRegistry building
     */
    private void exampleFir(){
        BootstrapServiceRegistryBuilder builder = new BootstrapServiceRegistryBuilder();
        // add a custom ClassLoader
//        builder.applyClassLoader(customClassLoader);
        builder.applyClassLoader(null);
        // manually add an Integrator
//        builder.applyIntegrator(customIntegrator);
        builder.applyIntegrator(null);
        BootstrapServiceRegistry registry = builder.build();
    }
}
