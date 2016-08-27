package com.epages.microservice.handson.hibernate;

import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class HibernateIntegrator implements Integrator {

    @Override
    public void integrate(Configuration configuration,
                          SessionFactoryImplementor sessionFactory,
                          SessionFactoryServiceRegistry serviceRegistry) {
        
        EventListenerRegistry eventListenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);

        EntityAuditListener listener = new EntityAuditListener(); 

        eventListenerRegistry.appendListeners(EventType.POST_INSERT, listener);
        eventListenerRegistry.appendListeners(EventType.PRE_UPDATE, listener);
        eventListenerRegistry.appendListeners(EventType.PRE_DELETE, listener);
    }

    @Override
    public void integrate(MetadataImplementor metadata, 
                          SessionFactoryImplementor sessionFactory, 
                          SessionFactoryServiceRegistry serviceRegistry) {
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, 
                             SessionFactoryServiceRegistry serviceRegistry) {
    }

}
