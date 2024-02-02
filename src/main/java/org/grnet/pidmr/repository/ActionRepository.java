package org.grnet.pidmr.repository;

import jakarta.enterprise.context.ApplicationScoped;
import org.grnet.pidmr.entity.database.Action;


@ApplicationScoped
public class ActionRepository implements Repository<Action, String>{
}
