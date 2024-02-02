package org.grnet.pidmr.repository;

import jakarta.enterprise.context.ApplicationScoped;
import org.grnet.pidmr.entity.database.ManageableEntity;


@ApplicationScoped
public class ManageableEntityRepository implements Repository<ManageableEntity, Long> {

    public ManageableEntity findByIdAndEntityType(Long id, String type){

        return find("from ManageableEntity me where me.id = ?1 and me.entityType = ?2", id, type).stream().findAny().get();
    }
}
