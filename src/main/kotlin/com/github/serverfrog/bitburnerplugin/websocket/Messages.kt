package com.github.serverfrog.bitburnerplugin.websocket

data class Response(val jsonrpc: String, val id: Int, val result: Any?, val error: Any?)

data class RequestParams(
    val filename: String?,
    val content: String?,
    val server: String,
)

abstract class AbstractRequest {
    val jsonrpc = "2.0"
    abstract val id: Int
    abstract val method: String
    abstract val params: RequestParams?
}

class PushFileRequest(override val id: Int, override val params: RequestParams) : AbstractRequest() {
    override val method = "pushFile"

    init {
        if (params.content == null) {
            throw UnsupportedOperationException("params.content is missing")
        } else if (params.filename == null) {
            throw UnsupportedOperationException("params.filename is missing")
        }
    }
}

class GetFileRequest(override val id: Int, override val params: RequestParams) : AbstractRequest() {

    override val method = "getFile"

    init {
        if (params.content != null) {
            throw UnsupportedOperationException("params.content is set")
        } else if (params.filename == null) {
            throw UnsupportedOperationException("params.filename is missing")
        }
    }
}

class DeleteFileRequest(override val id: Int, override val params: RequestParams) : AbstractRequest() {
    override val method = "deleteFile"

    init {
        if (params.content != null) {
            throw UnsupportedOperationException("params.content is set")
        } else if (params.filename == null) {
            throw UnsupportedOperationException("params.filename is missing")
        }
    }
}

class GetFileNamesRequest(override val id: Int, override val params: RequestParams) : AbstractRequest() {
    override val method = "getFileNames"

    init {
        if (params.content != null) {
            throw UnsupportedOperationException("params.content is set")
        } else if (params.filename != null) {
            throw UnsupportedOperationException("params.filename is set")
        }
    }
}

class GetAllFilesRequest(override val id: Int, override val params: RequestParams) : AbstractRequest() {
    override val method = "getAllFiles"

    init {
        if (params.content != null) {
            throw UnsupportedOperationException("params.content is set")
        } else if (params.filename != null) {
            throw UnsupportedOperationException("params.filename is set")
        }
    }
}

class CalculateRamRequest(override val id: Int, override val params: RequestParams) : AbstractRequest() {
    override val method = "calculateRam"

    init {
        if (params.content != null) {
            throw UnsupportedOperationException("params.content is set")
        } else if (params.filename == null) {
            throw UnsupportedOperationException("params.filename is missing")
        }
    }
}

class GetDefinitionFileRequest(override val id: Int) : AbstractRequest() {
    override val method = "getDefinitionFile"
    override val params = null
}
