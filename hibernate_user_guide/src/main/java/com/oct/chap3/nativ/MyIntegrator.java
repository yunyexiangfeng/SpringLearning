package com.oct.chap3.nativ;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.internal.DefaultAutoFlushEventListener;
import org.hibernate.event.internal.DefaultMergeEventListener;
import org.hibernate.event.internal.DefaultPersistEventListener;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

/**
 * @Author: Administrator
 * @CreateDate: 2020/12/27 15:26
 * @Description: Example 269. Configuring an event listener
 */
public class MyIntegrator implements Integrator {

    /**
     * Perform integration.
     *
     * @param metadata        The "compiled" representation of the mapping information
     * @param sessionFactory  The session factory being created
     * @param serviceRegistry The session factory's service registry
     */
    public void integrate(Metadata metadata,
                          SessionFactoryImplementor sessionFactory,
                          SessionFactoryServiceRegistry serviceRegistry) {
        // As you might expect, an EventListenerRegistry is the thing with which event
        // listeners are registered
        // It is a service so we look it up using the service registry
        final EventListenerRegistry eventListenerRegistry =
                serviceRegistry.getService( EventListenerRegistry.class );

        // If you wish to have custom determination and handling of "duplicate" listeners,
        // you would have to add an implementation of the
        // org.hibernate.event.service.spi.DuplicationStrategy contract like this
//        eventListenerRegistry.addDuplicationStrategy( new CustomDuplicationStrategy() );
        eventListenerRegistry.addDuplicationStrategy(null);

        // EventListenerRegistry defines 3 ways to register listeners:

        // 1) This form overrides any existing registrations with
        eventListenerRegistry.setListeners( EventType.AUTO_FLUSH,
                DefaultAutoFlushEventListener.class );

        // 2) This form adds the specified listener(s) to the beginning of the listener chain
        eventListenerRegistry.prependListeners( EventType.PERSIST,
                DefaultPersistEventListener.class );

        // 3) This form adds the specified listener(s) to the end of the listener chain
        eventListenerRegistry.appendListeners( EventType.MERGE,
                DefaultMergeEventListener.class );
    }

    /**
     * Tongue-in-cheek name for a shutdown callback.
     *
     * @param sessionFactory  The session factory being closed.
     * @param serviceRegistry That session factory's service registry
     */
    public void disintegrate(SessionFactoryImplementor sessionFactory,
                             SessionFactoryServiceRegistry serviceRegistry) {

    }
}
