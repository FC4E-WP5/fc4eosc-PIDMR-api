package org.grnet.pidmr.entity;


import okhttp3.FormBody;
import okhttp3.RequestBody;

public abstract class AbstractProvider {

    public abstract int getCharactersToBeRemoved();

    public abstract String getType();

    public String resolve(MetaResolver metaResolver, String pid) {
        return metaResolver.getLocation().concat(calibratePid(pid));
    }

    /**
     * To be able to resolve some pids, it is necessary to remove some of its first characters.
     * This method calibrates the pid by removing the number of characters declared in the "charactersToBeRemoved" property.
     *
     * @param pid The pid to be calibrated.
     * @return The calibrated pid.
     */
    public String calibratePid(String pid) {

        if (getCharactersToBeRemoved() != 0) {
            return pid.substring(getCharactersToBeRemoved());
        } else {
            return pid;
        }

    }

    public RequestBody getRequestBody(String pid, String mode, String bodyAttribute, String bodyAttributePrefix, String appendParam) {

        return new FormBody.Builder()
                .addEncoded(bodyAttribute, bodyAttributePrefix +
                        calibratePid(pid) +
                        appendParam +
                        mode)
                .build();
    }
}