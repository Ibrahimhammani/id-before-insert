package org.ibrahimhammani.securedb.idbeforeinsert.entitylistener;

import lombok.RequiredArgsConstructor;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventListenerIntegrator implements Integrator {

    private final PreInsertListener preInsertListener;

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        EventListenerRegistry eventListenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);
        /*
            Register the event listener
         */
        eventListenerRegistry.appendListeners(EventType.PRE_INSERT, preInsertListener);
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
    }
}
