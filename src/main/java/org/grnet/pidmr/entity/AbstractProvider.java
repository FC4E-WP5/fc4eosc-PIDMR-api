package org.grnet.pidmr.entity;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public interface AbstractProvider {

    int getCharactersToBeRemoved();

    String getType();

    boolean directResolution();

    default String resolve(MetaResolver metaResolver, String pid) {
        return metaResolver.getLocation().concat(calibratePid(pid));
    }

    /**
     * To be able to resolve some pids, it is necessary to remove some of its first characters.
     * This method calibrates the pid by removing the number of characters declared in the "charactersToBeRemoved" property.
     *
     * @param pid The pid to be calibrated.
     * @return The calibrated pid.
     */
    default String calibratePid(String pid) {

        if (getCharactersToBeRemoved() != 0) {

            if (pid.startsWith("doi")) {

                return pid.substring(getCharactersToBeRemoved());
            } else {

                return pid;
            }
        } else {
            return pid;
        }
    }

    default RequestBody getRequestBody(String pid, String mode, String bodyAttribute, String bodyAttributePrefix, String appendParam) {

        if(directResolution()){

            return new FormBody.Builder()
                    .addEncoded(bodyAttribute,
                            calibratePid(pid))
                    .build();
        } else {

            return new FormBody.Builder()
                    .addEncoded(bodyAttribute, bodyAttributePrefix +
                            calibratePid(pid) +
                            appendParam +
                            mode)
                    .build();
        }
    }
    void isModeSupported(String mode);
}