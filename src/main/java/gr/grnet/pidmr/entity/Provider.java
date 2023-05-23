package gr.grnet.pidmr.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
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
    @JsonProperty(value = "type", required = true)
    private String type;
    private String name;
    private String description;
    @JsonProperty(value = "regex", required = true)
    private Set<String> regex;
    @JsonProperty(value = "characters_to_be_removed")
    //To be able to resolve some pids, it is necessary to remove some of its first characters.
    private int charactersToBeRemoved;
    @JsonProperty(value = "metaresolver", required = true)
    private String metaresolver;
    @JsonProperty(value = "check_type_with_regex")
    //Search for Providers by regex instead of variable type.
    private boolean checkTypeWithRegex;


    private Set<String> actions = new HashSet<>();

    /**
     * To be able to resolve some pids, it is necessary to remove some of its first characters.
     * This method calibrates the pid by removing the number of characters declared in the "charactersToBeRemoved" property.
     * @param pid The pid to be calibrated.
     * @return The calibrated pid.
     */
    public String calibratePid(String pid){

        if(charactersToBeRemoved!=0){
            return pid.substring(charactersToBeRemoved);
        } else {
            return pid;
        }
    }
}