package gr.grnet;

import io.quarkus.test.junit.QuarkusTestProfile;

public class MetaResolverTestProfile implements QuarkusTestProfile {

    @Override
    public boolean disableApplicationLifecycleObservers() {
        return true;
    }
}
