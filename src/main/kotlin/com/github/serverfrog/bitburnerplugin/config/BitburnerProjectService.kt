package com.github.serverfrog.bitburnerplugin.config

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class BitburnerProjectService(private val project: Project) {

    companion object {
        private const val PROJECT_FOLDER_KEY = "BITBURNER_PROJECT_FOLDER"
    }

    fun getProjectFolder(): String? {
        return PropertiesComponent.getInstance(project).getValue(PROJECT_FOLDER_KEY)
    }

    fun setProjectFolder(folder: String) {
        PropertiesComponent.getInstance(project).setValue(PROJECT_FOLDER_KEY, folder)
    }
}
