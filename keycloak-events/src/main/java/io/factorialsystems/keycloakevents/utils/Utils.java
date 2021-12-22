package io.factorialsystems.keycloakevents.utils;

public class Utils {

    public static String toComponentIdString(Object object) {

        if (object == null) {
            return null;
        }

        return object.getClass().getSimpleName() + System.identityHashCode(object);
    }
}
