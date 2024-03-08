package org.grnet.pidmr.repository;

import jakarta.enterprise.context.ApplicationScoped;
import org.grnet.pidmr.entity.database.Regex;
import org.grnet.pidmr.enums.ProviderStatus;

import java.util.List;


@ApplicationScoped
public class RegexRepository implements Repository<Regex, Long>{

    public List<Regex> findAllRegexesBelongsToApprovedProviders(){

        return find("from Regex r where r.provider.status = ?1", ProviderStatus.APPROVED).list();
    }
}
