package com.github.serverfrog.bitburnerplugin.services

import com.intellij.openapi.project.Project
import com.github.serverfrog.bitburnerplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
