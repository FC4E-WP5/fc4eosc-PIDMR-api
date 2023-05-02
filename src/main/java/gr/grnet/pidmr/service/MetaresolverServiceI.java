package gr.grnet.pidmr.service;

import gr.grnet.pidmr.entity.MetaResolver;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface MetaresolverServiceI {

     String resolve(MetaResolver metaResolver, String pid);

     String resolve(MetaResolver metaResolver, String pid, String mode);

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
