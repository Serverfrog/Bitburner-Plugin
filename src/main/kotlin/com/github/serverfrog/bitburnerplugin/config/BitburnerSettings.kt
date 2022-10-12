package com.github.serverfrog.bitburnerplugin.config

import com.github.serverfrog.bitburnerplugin.MyBundle
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.options.ConfigurableBase

private const val AUTH_TOKEN = "AUTH_TOKEN"

class BitburnerSettings : ConfigurableBase<BitBurnerSettingsUi, BitburnerSettings>(
    "com.github.serverfrog.bitburnerplugin.config.BitburnerSettings", MyBundle.message("name"),
    "com.github.serverfrog.bitburnerplugin.config.BitburnerSettings"
) {

    override fun getSettings(): BitburnerSettings {
        return BitburnerSettings()
    }

    override fun createUi(): BitBurnerSettingsUi {
        return BitBurnerSettingsUi()
    }

    companion object {
        fun getAuthToken(): String? {
            val credentialAttributes = createCredentialAttributes()
            return PasswordSafe.instance.getPassword(credentialAttributes)
        }

        fun setAuthToken(authToken: String) {
            val credentialAttributes = createCredentialAttributes()

            val credentials = Credentials("", authToken)
            PasswordSafe.instance.set(credentialAttributes, credentials)
        }

        private fun createCredentialAttributes(): CredentialAttributes {
            return CredentialAttributes(
                generateServiceName(MyBundle.message("name"), AUTH_TOKEN)
            )
        }
    }
}
