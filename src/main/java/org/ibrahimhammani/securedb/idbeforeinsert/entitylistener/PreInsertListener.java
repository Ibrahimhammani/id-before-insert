package org.ibrahimhammani.securedb.idbeforeinsert.entitylistener;

import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.ibrahimhammani.securedb.idbeforeinsert.entity.Odd;
import org.ibrahimhammani.securedb.idbeforeinsert.entitylistener.exception.UnknownId;
import org.springframework.stereotype.Component;

@Component
public class PreInsertListener implements PreInsertEventListener {

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        Odd entityCasted = (Odd) event.getEntity();
        if (entityCasted.getId() == null) {
            throw new UnknownId("Entity id is null");
        }
        Boolean isOdd = (entityCasted.getId() % 2 == 0);
        /*
            update the state of the entity
            this is enough for saving the right values in the database
        */
        event.getState()[ArrayUtils.indexOf(
                event.getPersister().getEntityMetamodel().getPropertyNames(),
                "isOdd")] = isOdd;

        /*
            Even if the right state is saved in the database
            hibernate will not update the entity field, we need to do this by ourselves
        */
        entityCasted.setIsOdd(isOdd);
        return false;
    }
}
