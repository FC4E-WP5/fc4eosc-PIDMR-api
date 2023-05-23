package gr.grnet.pidmr.service;

import gr.grnet.pidmr.dto.Validity;
import gr.grnet.pidmr.entity.Action;
import gr.grnet.pidmr.entity.Provider;
import io.quarkus.cache.CacheResult;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface ProviderServiceI {

     Validity valid(String pid);

     Validity valid(String pid, String type);

     Set<Provider> getProviders();

     Set<Action> getActions();

    /**
     * Each identifier should match the regular expression provided by its Provider.
     *
     * @param pid The pid to be validated.
     * @param provider The provider that pid belongs to.
     * @return An object representing whether a PID is valid.
     */
     default Validity valid(Provider provider, String pid){

        var regex = provider.getRegex();

        var valid = regex
                .stream()
                .anyMatch(pid::matches);

        var validity = new Validity();
        validity.valid = valid;
        validity.type = provider.getType();

        return validity;
    }

    default Validity validation(String pid, String type){

        if(type.isEmpty()){
            return valid(pid);
        } else{
            return valid(pid, type);
        }
    }

    default Map<String, Action> actionsToMap(){

        return getActions().stream()
                .collect(Collectors.toMap(Action::getId, Function.identity()));
    }

    /**
     * This method returns the Provider that corresponds to a particular type.
     *
     * @param type The type of Provider.
     * @return The corresponding Provider.
     */

    @CacheResult(cacheName = "providersToMap")
    default Provider getProviderByType(String type){

        var map = getProviders()
                .stream()
                .collect(Collectors.toMap(Provider::getType, Function.identity()));

        return map.get(type);
    }

    /**
     * This method checks if the incoming pid belongs to a particular type.
     * @param pid The incoming pid.
     * @param type The type of pid to which the incoming pid may belong.
     * @return If the incoming pid belongs to the given type.
     */
    default boolean belongsTo(String pid, String type){

        return IntStream
                .range(0, type.length())
                .allMatch(index->Character.toLowerCase(type.charAt(index)) == Character.toLowerCase(pid.charAt(index)));
    }

    /**
     * This method finds and returns the pid type. If there is no available pid type, it returns an empty Optional object.
     *
     * @param pid The incoming pid.
     * @return The pid type.
     */
    default Optional<String> getPidType(String pid){

        var providers = getProviders();

        var optionalType = providers
                .stream()
                .map(Provider::getType)
                .filter(tp->belongsTo(pid, tp))
                .findAny();

        if(optionalType.isPresent()){

            return optionalType;
        } else{

            return providers
                    .stream()
                    .filter(Provider::isCheckTypeWithRegex)
                    .filter(pr->{

                        var regex = pr.getRegex();

                        return regex
                                .stream()
                                .anyMatch(pid::matches);
                    })
                    .map(Provider::getType)
                    .findAny();
        }
    }
}
