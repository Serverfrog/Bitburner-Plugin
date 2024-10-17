package com.github.serverfrog.bitburnerplugin.config

import com.intellij.openapi.components.Service

@Service
class BitburnerApplicationService {
    companion object {

        private var testPort = 0

        fun getSystemPort(): Int {
            return testPort
        }

        fun setSystemPort(port: Int) {
            testPort = port
        }
    }
}
