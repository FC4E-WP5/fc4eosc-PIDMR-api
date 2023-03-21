package gr.grnet.pidmr.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * The available Metaresolvers are declared in a configuration file named resolvers.conf as JSON objects.
 * Those objects are represented by the MetaResolver class.
 */
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MetaResolver {

    @EqualsAndHashCode.Include
    private String key;
    private String location;
    private String description;
}