package org.grnet.pidmr.service;

import org.grnet.pidmr.entity.database.ManageableEntity;
import org.grnet.pidmr.repository.Repository;

import javax.persistence.EntityNotFoundException;

public abstract class Service {

    public <E, ID> E checkIfEntityExists(Repository<E, ID> repository, ID id, String message){

        var optional = repository.findByIdOptional(id);

        return optional.orElseThrow(()-> new EntityNotFoundException(String.format("{%s} with ID {%s} doesn't exist", message, id)));
    }

    public <ID> void canManageEntity(Repository<? extends ManageableEntity, ID> repository, ID id){


    }
}
