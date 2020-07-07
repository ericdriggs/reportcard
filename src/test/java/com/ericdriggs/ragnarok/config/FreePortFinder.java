//package com.ericdriggs.ragnarok.config;
//
//import java.io.IOException;
//import java.net.ServerSocket;
//
//public enum FreePortFinder {
//
//    ;
//    /**
//     * Returns a free port number on localhost.
//     *
//     * Heavily inspired from org.eclipse.jdt.launching.SocketUtil (to avoid a dependency to JDT just because of this).
//     * Slightly improved with close() missing in JDT. And throws exception instead of returning -1.
//     *
//     * @return a free port number on localhost
//     * @throws IllegalStateException if unable to find a free port
//     */
//    public static int findFreeLocalPort() {
//        ServerSocket socket = null;
//        try {
//            socket = new ServerSocket(0);
//            socket.setReuseAddress(true);
//            int port = socket.getLocalPort();
//            try {
//                socket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return port;
//        } catch (IOException  e) {
//            e.printStackTrace();
//        } finally {
//            if (socket != null) {
//                try {
//                    socket.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        throw new IllegalStateException("Could not find a free TCP/IP port to start embedded Jetty HTTP Server on");
//    }
//}
