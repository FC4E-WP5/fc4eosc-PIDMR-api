package org.grnet.pidmr.repository;

import jakarta.enterprise.context.ApplicationScoped;
import org.grnet.pidmr.entity.database.RoleChangeRequest;

@ApplicationScoped
public class RoleChangeRequestsRepository implements Repository<RoleChangeRequest, String>{
}
