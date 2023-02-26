package ir.amirab.debugboard.utils

import java.net.Inet4Address
import java.net.NetworkInterface

internal fun getLocalIpAddress(): List<String> {
    return runCatching {
        NetworkInterface.getNetworkInterfaces()
            .toList()
            .flatMap { networkInterface ->
                networkInterface.inetAddresses
                    .toList()
                    .filter {
                        !it.isLinkLocalAddress && !it.isLoopbackAddress && it is Inet4Address
                    }
            }
            .mapNotNull { it.hostAddress }
    }.getOrElse {
        it.printStackTrace()
        emptyList()
    }
}