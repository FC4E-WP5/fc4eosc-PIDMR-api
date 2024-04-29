package org.grnet.pidmr.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import org.grnet.pidmr.exception.ModeIsNotSupported;
import org.grnet.pidmr.mapper.ProviderMapper;

import java.util.HashSet;
import java.util.Set;

/**
 * The available Metaresolvers are declared in a configuration file named providers.conf as JSON objects.
 * Those objects are represented by the Provider class.
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Provider implements AbstractProvider {

    @EqualsAndHashCode.Include
    @JsonProperty(value = "type", required = true)
    private String type;
    private String name;
    private String description;
    @JsonProperty(value = "regex", required = true)
    private Set<String> regex;
    @JsonProperty(value = "metaresolver", required = true)
    private String metaresolver;
    @JsonProperty(value = "check_type_with_regex")
    //Search for Providers by regex instead of variable type.
    private boolean checkTypeWithRegex;

    @JsonProperty(value = "characters_to_be_removed")
    //To be able to resolve some pids, it is necessary to remove some of its first characters.
    private int charactersToBeRemoved;

    private Set<String> actions = new HashSet<>();

    public String getType() {
        return type;
    }

    @Override
    public boolean directResolution() {
        return false;
    }

    @Override
    public void isModeSupported(String mode) {

        ProviderMapper.INSTANCE.actions(getActions())
                .stream()
                .filter(action -> action.mode.equals(mode))
                .findAny()
                .orElseThrow(() -> new ModeIsNotSupported(String.format("This mode {%s} is not supported.", mode)));
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getRegex() {
        return regex;
    }

    public void setRegex(Set<String> regex) {
        this.regex = regex;
    }

    public String getMetaresolver() {
        return metaresolver;
    }

    public void setMetaresolver(String metaresolver) {
        this.metaresolver = metaresolver;
    }

    public boolean isCheckTypeWithRegex() {
        return checkTypeWithRegex;
    }

    public void setCheckTypeWithRegex(boolean checkTypeWithRegex) {
        this.checkTypeWithRegex = checkTypeWithRegex;
    }

    public int getCharactersToBeRemoved() {
        return charactersToBeRemoved;
    }

    public void setCharactersToBeRemoved(int charactersToBeRemoved) {
        this.charactersToBeRemoved = charactersToBeRemoved;
    }

    public Set<String> getActions() {
        return actions;
    }

    public void setActions(Set<String> actions) {
        this.actions = actions;
    }

}