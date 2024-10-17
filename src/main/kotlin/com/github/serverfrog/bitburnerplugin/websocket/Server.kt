package com.github.serverfrog.bitburnerplugin.websocket

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.serverfrog.bitburnerplugin.config.BitburnerSettings
import com.intellij.openapi.diagnostic.thisLogger
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer

class Server(val settings: BitburnerSettings, val messageHandler: MessageHandler) :
    WebSocketServer(java.net.InetSocketAddress(12525)) {


    var clients = ArrayList<WebSocket>()

    val mapper = jacksonObjectMapper()

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        clients.add(conn!!)
        val queue = messageHandler.project.getService(CommunicationQueue::class.java)
        queue.onClientsConnected()
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        clients.remove(conn!!)
        if (clients.isEmpty()) {
            val queue = messageHandler.project.getService(CommunicationQueue::class.java)
            queue.noClients()
        }
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        val responseValue = mapper.readValue<Response>(message!!)
        messageHandler.handleResponse(responseValue)
    }

    override fun onError(conn: WebSocket?, ex: java.lang.Exception?) {
        thisLogger().error(ex.toString(), ex)
    }

    override fun onStart() {
        messageHandler.setUp(this)

        val queue = messageHandler.project.getService(CommunicationQueue::class.java)
        queue.onServerStarted()
    }

    fun getClients(): List<WebSocket> {
        return clients.toList()
    }
}
