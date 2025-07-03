package org.grnet.pidmr.service;

import jakarta.ws.rs.core.UriInfo;
import org.grnet.pidmr.dto.Identification;
import org.grnet.pidmr.dto.ProviderDto;
import org.grnet.pidmr.dto.Validity;
import org.grnet.pidmr.entity.AbstractProvider;
import org.grnet.pidmr.pagination.PageResource;

import java.util.Set;
import java.util.stream.IntStream;

public interface ProviderServiceI {

    /**
     * This method checks whether a given Personal Identification Number (PID) is valid based on the regular expressions provided by different Providers.
     *
     * @param pid The pid to be checked.
     * @return An object that represents whether the PID is valid according to any of the provided regular expressions.
     */
    Validity valid(String pid);

    /**
     * This method accepts the provider type and checks whether a given Personal Identification Number (PID) is valid based on the regular expressions provided by this Provider.
     *
     * @param pid The pid to be checked.
     * @param type The provider type.
     * @return An object that represents whether the PID is valid according to any of the provided regular expressions by this Provider.
     */
    Validity valid(String pid, String type);

    AbstractProvider getProviderByPid(String pid);

    /**
     * This method is responsible for paginating the available Providers.
     * It returns from a specific page as many Providers as the client requests.
     *
     * @param page    Indicates the page number.
     * @param size    The total number of Providers to be retrieved.
     * @param uriInfo The current uri.
     * @return An object represents the paginated results.
     */
    PageResource<ProviderDto> pagination(int page, int size, UriInfo uriInfo);

    default Validity validation(String pid, String type) {

        if (type.isEmpty()) {
            return valid(pid);
        } else {
            return valid(pid, type);
        }
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
     * This method identifies PIDs from the provided text.
     *
     * @param text The text to be checked for PID.
     * @return A list containing identification status, possible type, and an example of PID.
     */
    Set<Identification> multipleIdentification(String text);

    /**
     * This method identifies PIDs from the provided text.
     *
     * @param text The text to be checked for PID.
     * @return An object containing identification status, possible type, and an example of PID.
     */
    Identification identify(String text);

}
