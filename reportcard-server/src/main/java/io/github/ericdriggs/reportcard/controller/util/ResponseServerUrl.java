package io.github.ericdriggs.reportcard.controller.util;

public enum ResponseServerUrl {
    ;

    public static String getServerUrl() {
        return serverUrl;
    }

    final static String serverUrl = initServerUrl();

    static String initServerUrl() {

        String serverUrlEnv = System.getenv("REPORTCARD_SERVER_URL");
        if (serverUrlEnv == null) {
            return "";
        }
        return serverUrlEnv.trim().replaceAll("/$", "");
    }
}
