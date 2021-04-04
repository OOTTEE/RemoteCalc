package me.ote.polishcalc.server;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Collections;
import java.util.Map;

public class ServerProfiles {

    public static class CustomPort implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Collections.singletonMap("service.listen-port", "50000");
        }
    }

}
