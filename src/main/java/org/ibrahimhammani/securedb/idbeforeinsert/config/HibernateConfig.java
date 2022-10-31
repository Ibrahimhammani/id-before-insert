package org.ibrahimhammani.securedb.idbeforeinsert.config;

import lombok.RequiredArgsConstructor;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.ibrahimhammani.securedb.idbeforeinsert.entitylistener.EventListenerIntegrator;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HibernateConfig implements HibernatePropertiesCustomizer {

    private final EventListenerIntegrator eventListenerIntegrator;

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(
                "hibernate.integrator_provider",
                (IntegratorProvider) () -> Collections.singletonList(eventListenerIntegrator)
        );
    }
}
