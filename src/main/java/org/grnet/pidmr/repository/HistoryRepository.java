package org.grnet.pidmr.repository;

import jakarta.enterprise.context.ApplicationScoped;
import org.grnet.pidmr.entity.database.History;

@ApplicationScoped
public class HistoryRepository implements Repository<History, Long> {
}
