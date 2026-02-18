package com.aetheria.mmo.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector3
import com.aetheria.mmo.components.*
import com.aetheria.mmo.net.Packet
import com.aetheria.mmo.utils.Constants
import com.aetheria.mmo.utils.Logger
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Network Sync System
 * Handles client-side prediction, server reconciliation, and snapshot interpolation
 * Implements the authoritative server model with client-side prediction
 */
class NetworkSyncSystem : IteratingSystem(
    Family.all(NetworkComponent::class.java, TransformComponent::class.java).get()
) {

    private val networkMapper = ComponentMapper.getFor(NetworkComponent::class.java)
    private val transformMapper = ComponentMapper.getFor(TransformComponent::class.java)
    private val velocityMapper = ComponentMapper.getFor(VelocityComponent::class.java)

    // Snapshot buffer for interpolation
    private val snapshotBuffer = mutableMapOf<String, MutableList<Snapshot>>()

    // Outgoing packet queue
    private val outgoingPackets = ConcurrentLinkedQueue<Packet>()

    // Incoming packet queue
    private val incomingPackets = ConcurrentLinkedQueue<Packet>()

    // Network statistics
    private var lastSyncTime = 0L
    private var syncInterval = 1000L / Constants.CLIENT_UPDATE_RATE // milliseconds
    private var ping = 0L

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val network = networkMapper.get(entity)
        val transform = transformMapper.get(entity)

        if (network.isLocalPlayer) {
            // Client-side prediction for local player
            handleLocalPlayer(entity, network, transform, deltaTime)
        } else {
            // Snapshot interpolation for remote players
            handleRemotePlayer(entity, network, transform, deltaTime)
        }
    }

    /**
     * Handle local player (client-side prediction)
     */
    private fun handleLocalPlayer(entity: Entity, network: NetworkComponent, transform: TransformComponent, deltaTime: Float) {
        val currentTime = System.currentTimeMillis()

        // Send position updates to server at fixed rate
        if (currentTime - lastSyncTime >= syncInterval) {
            sendPositionUpdate(network.networkId, transform)
            lastSyncTime = currentTime
        }

        // Process server reconciliation
        processServerReconciliation(entity, network)
    }

    /**
     * Handle remote player (snapshot interpolation)
     */
    private fun handleRemotePlayer(entity: Entity, network: NetworkComponent, transform: TransformComponent, deltaTime: Float) {
        val snapshots = snapshotBuffer[network.networkId] ?: return

        if (snapshots.size < 2) return

        // Get interpolation time (render time is slightly behind current time)
        val renderTime = System.currentTimeMillis() - Constants.INTERPOLATION_DELAY

        // Find two snapshots to interpolate between
        var snapshot0: Snapshot? = null
        var snapshot1: Snapshot? = null

        for (i in 0 until snapshots.size - 1) {
            if (snapshots[i].timestamp <= renderTime && renderTime <= snapshots[i + 1].timestamp) {
                snapshot0 = snapshots[i]
                snapshot1 = snapshots[i + 1]
                break
            }
        }

        if (snapshot0 != null && snapshot1 != null) {
            // Interpolate between snapshots
            val totalTime = (snapshot1.timestamp - snapshot0.timestamp).toFloat()
            val currentTime = (renderTime - snapshot0.timestamp).toFloat()
            val alpha = if (totalTime > 0) currentTime / totalTime else 0f

            // Lerp position
            transform.position.set(snapshot0.position).lerp(snapshot1.position, alpha)

            // Slerp rotation
            transform.rotation.set(snapshot0.rotation).slerp(snapshot1.rotation, alpha)

            // Clean up old snapshots
            snapshots.removeAll { it.timestamp < renderTime - 1000 }
        }
    }

    /**
     * Send position update to server
     */
    private fun sendPositionUpdate(playerId: String, transform: TransformComponent) {
        val packet = Packet.PlayerMove(
            playerId = playerId,
            x = transform.position.x,
            y = transform.position.y,
            z = transform.position.z,
            rotX = transform.rotation.x,
            rotY = transform.rotation.y,
            rotZ = transform.rotation.z,
            rotW = transform.rotation.w
        )
        outgoingPackets.offer(packet)
    }

    /**
     * Process server reconciliation (correct client prediction errors)
     */
    private fun processServerReconciliation(entity: Entity, network: NetworkComponent) {
        // TODO: Implement server reconciliation
        // When server sends authoritative position, check if it differs from predicted position
        // If difference is significant, snap to server position
        // Otherwise, smoothly correct over time
    }

    /**
     * Add snapshot for remote player
     */
    fun addSnapshot(playerId: String, position: Vector3, rotation: com.badlogic.gdx.math.Quaternion, timestamp: Long) {
        val snapshots = snapshotBuffer.getOrPut(playerId) { mutableListOf() }

        snapshots.add(Snapshot(position.cpy(), rotation.cpy(), timestamp))

        // Keep buffer size limited
        if (snapshots.size > Constants.SNAPSHOT_BUFFER_SIZE) {
            snapshots.removeAt(0)
        }
    }

    /**
     * Handle incoming packet
     */
    fun handlePacket(packet: Packet) {
        when (packet) {
            is Packet.PlayerMove -> {
                addSnapshot(
                    packet.playerId,
                    Vector3(packet.x, packet.y, packet.z),
                    com.badlogic.gdx.math.Quaternion(packet.rotX, packet.rotY, packet.rotZ, packet.rotW),
                    packet.timestamp
                )
            }
            is Packet.WorldSnapshot -> {
                packet.entities.forEach { entityState ->
                    addSnapshot(
                        entityState.id,
                        Vector3(entityState.x, entityState.y, entityState.z),
                        com.badlogic.gdx.math.Quaternion(entityState.rotX, entityState.rotY, entityState.rotZ, entityState.rotW),
                        packet.timestamp
                    )
                }
            }
            is Packet.Pong -> {
                ping = System.currentTimeMillis() - packet.clientTime
                Logger.network("Ping: ${ping}ms")
            }
            else -> {
                // Handle other packet types
            }
        }
    }

    /**
     * Get outgoing packets
     */
    fun getOutgoingPackets(): List<Packet> {
        val packets = mutableListOf<Packet>()
        while (outgoingPackets.isNotEmpty()) {
            outgoingPackets.poll()?.let { packets.add(it) }
        }
        return packets
    }

    /**
     * Send ping packet
     */
    fun sendPing() {
        val packet = Packet.Ping(clientTime = System.currentTimeMillis())
        outgoingPackets.offer(packet)
    }

    /**
     * Get current ping
     */
    fun getPing(): Long = ping

    /**
     * Clear snapshot buffer for player
     */
    fun clearSnapshots(playerId: String) {
        snapshotBuffer.remove(playerId)
    }

    /**
     * Clear all snapshots
     */
    fun clearAllSnapshots() {
        snapshotBuffer.clear()
    }

    /**
     * Snapshot data class
     */
    private data class Snapshot(
        val position: Vector3,
        val rotation: com.badlogic.gdx.math.Quaternion,
        val timestamp: Long
    )
}
