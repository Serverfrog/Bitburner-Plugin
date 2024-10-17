package com.github.serverfrog.bitburnerplugin.websocket

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.intellij.openapi.project.Project


typealias ResponseHandler = (Response) -> Unit

class MessageHandler(val project: Project) {

    val answers = mutableMapOf<Int, ResponseHandler>()
    var webSocketServer: Server? = null
    var messageCounter = 0

    val mapper = jacksonObjectMapper()


    fun setUp(webSocketServer: Server) {
        answers.clear()
        messageCounter = 0
        this.webSocketServer = webSocketServer
    }

    fun handleResponse(response: Response) {
        answers[response.id]?.let {
            it(response)
            answers.remove(response.id)
        }
    }

    private fun sendRequest(request: AbstractRequest) {

        val queue = project.getService(CommunicationQueue::class.java)
        queue.addTask {
            mapper.writeValueAsString(request)?.let { json ->
                for (clients in webSocketServer!!.getClients()) {
                    clients.send(json)
                }
            }
        }

    }

    fun pushFile(filename: String, content: String, server: String, responseHandler: ResponseHandler) {
        val counter = messageCounter++
        val request = PushFileRequest(counter, RequestParams(filename, content, server))
        answers[counter] = responseHandler
        sendRequest(request)
    }

    fun getFile(filename: String, server: String, responseHandler: ResponseHandler) {
        val counter = messageCounter++
        val request = GetFileRequest(counter, RequestParams(filename, null, server))
        answers[counter] = responseHandler
        sendRequest(request)
    }

    fun deleteFile(filename: String, server: String, responseHandler: ResponseHandler) {
        val counter = messageCounter++
        val request = DeleteFileRequest(counter, RequestParams(filename, null, server))
        answers[counter] = responseHandler
        sendRequest(request)
    }


    fun getFileNames(server: String, responseHandler: ResponseHandler) {
        val counter = messageCounter++
        val request = GetFileNamesRequest(counter, RequestParams(null, null, server))
        answers[counter] = responseHandler
        sendRequest(request)
    }


    fun getAllFiles(server: String, responseHandler: ResponseHandler) {
        val counter = messageCounter++
        val request = GetAllFilesRequest(counter, RequestParams(null, null, server))
        answers[counter] = responseHandler
        sendRequest(request)
    }


    fun calculateRam(filename: String, server: String, responseHandler: ResponseHandler) {
        val counter = messageCounter++
        val request = CalculateRamRequest(counter, RequestParams(filename, null, server))
        answers[counter] = responseHandler
        sendRequest(request)
    }


    fun getDefinitionFile(responseHandler: ResponseHandler) {
        val counter = messageCounter++
        val request = GetDefinitionFileRequest(counter)
        answers[counter] = responseHandler
        sendRequest(request)
    }

}
