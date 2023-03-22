package gr.grnet.pidmr.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * The available Metaresolvers are declared in a configuration file named metaresolvers.conf as JSON objects.
 * Those objects are represented by the MetaResolver class.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class MetaResolver {

    @EqualsAndHashCode.Include
    private String key;
    private String location;
    private String description;
}