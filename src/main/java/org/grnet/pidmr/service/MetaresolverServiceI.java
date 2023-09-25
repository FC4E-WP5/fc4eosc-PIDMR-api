package org.grnet.pidmr.service;

import org.grnet.pidmr.entity.MetaResolver;
import org.grnet.pidmr.entity.Provider;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface MetaresolverServiceI {

     String resolve(Provider provider, String pid, String mode);

     String resolve(String pid, String mode);

     Set<MetaResolver> getMetaresolvers();

     /**
      * This method returns the Metaresolver that corresponds to a particular key.
      *
      * @param key The Metaresolver key as defined in the metaresolvers.conf file.
      * @return The corresponding Metaresolver.
      */
     default MetaResolver getMetaresolverByKey(String key) {

          var metaResolvers = getMetaresolvers();

          Map<String, MetaResolver> map = metaResolvers.stream()
                  .collect(Collectors.toMap(MetaResolver::getKey, Function.identity()));

          return map.get(key);
     }
}
