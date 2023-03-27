package gr.grnet.pidmr.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * The available Metaresolvers are declared in a configuration file named providers.conf as JSON objects.
 * Those objects are represented by the Provider class.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class Provider {

    @EqualsAndHashCode.Include
    private String type;
    private String name;
    private String description;
    private Set<String> regex;
}
