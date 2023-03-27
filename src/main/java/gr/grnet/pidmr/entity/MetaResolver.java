package gr.grnet.pidmr.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(value = "key", required = true)
    private String key;
    @JsonProperty(value = "location", required = true)
    private String location;
    private String description;
}