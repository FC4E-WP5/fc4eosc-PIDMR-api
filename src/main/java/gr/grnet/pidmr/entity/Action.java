package gr.grnet.pidmr.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * The available Actions are declared in a configuration file named actions.conf as JSON objects.
 * Those objects are represented by the Action class.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class Action {

    @EqualsAndHashCode.Include
    @JsonProperty(value = "id", required = true)
    private String id;
    @JsonProperty(value = "mode", required = true)
    private String mode;
    @JsonProperty(value = "name", required = true)
    private String name;

}
