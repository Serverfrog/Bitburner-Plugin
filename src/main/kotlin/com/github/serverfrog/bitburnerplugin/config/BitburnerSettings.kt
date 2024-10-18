package com.github.serverfrog.bitburnerplugin.config

import com.github.serverfrog.bitburnerplugin.MyBundle
import com.intellij.openapi.options.ConfigurableBase


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

    fun getSystemPort(): Int {
        return BitburnerApplicationService.getSystemPort()
    }

    fun setSystemPort(port: Int) {
        BitburnerApplicationService.setSystemPort(port)
    }

}
