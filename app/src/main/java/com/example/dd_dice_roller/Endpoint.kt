package com.example.dd_dice_roller

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo

// Exercise
class Endpoint(val id: String, private val info: DiscoveredEndpointInfo) {
    val name
        get() = info.endpointName
    override fun toString() = name
}
