package com.cael.omr.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

@Slf4j
public class InetAddressUtil {
    public static String getMacAddress() {

        InetAddress ip;
        try {

            ip = InetAddress.getLocalHost();
            System.out.println("Current IP address : " + ip.getHostAddress());

            NetworkInterface network = NetworkInterface.getByInetAddress(ip);

            byte[] mac = network.getHardwareAddress();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }

            System.out.print("Current MAC address : " + sb.toString());
            System.out.println("---");
            return sb.toString();

        } catch (UnknownHostException e) {

            log.error("UnknownHostException getMacAddress: ", e);

        } catch (SocketException e) {

            log.error("SocketException getMacAddress: ", e);

        }
        return "";
    }
}