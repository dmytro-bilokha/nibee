package com.dmytrobilokha.nibee.dao;

public class H2dbMySqlFunctions {

    private H2dbMySqlFunctions() {
        //Not going to instantiate the class
    }

    public static byte[] uuidToBin(String uuid, Boolean swap) {
        if (uuid == null) {
            return null;
        }
        String hexString = uuid.toUpperCase().replaceAll("-", "");
        if (Boolean.TRUE.equals(swap)) {
            hexString = hexString.substring(12, 16) + hexString.substring(8, 12) + hexString.substring(0, 8)
                    + hexString.substring(16);
        }
        byte[] data = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                             + Character.digit(hexString.charAt(i+1), 16));
        }
        return data;
    }

    //MySQL INET6_ATON works with both IPv4 and IPv6 address types. Here I've implemented only IPv4, because for
    //testing it should be enough.
    public static byte[] inet6AtoN(String ipString) {
        String[] byteStrings = ipString.split("\\.");
        byte[] data = new byte[byteStrings.length];
        for (int i = 0; i < byteStrings.length; i++) {
            data[i] = (byte) Integer.parseInt(byteStrings[i], 10);
        }
        return data;
    }

}
