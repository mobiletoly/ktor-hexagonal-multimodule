fun getSubprojectList() = subprojects + project

@Suppress("UnstableApiUsage")
task<JacocoMerge>("jacocoMergeReport") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Merge Jacoco data files from all subprojects into one"
    afterEvaluate {
        val execFiles = objects.fileCollection()
        getSubprojectList().forEach { subProject: Project ->
            if (subProject.pluginManager.hasPlugin("jacoco")) {
                val testTasks = subProject.tasks.withType<Test>()
                // ensure that .exec files are actually present
                dependsOn(testTasks)
                testTasks.forEach { task: Test ->
                    // The JacocoTaskExtension is the source of truth for the location of the .exec file.
                    val extension = task.extensions.findByType(JacocoTaskExtension::class.java)
                    extension?.let {
                        execFiles.from(it.destinationFile)
                    }
                }
            }
        }
        executionData = execFiles
    }
    doFirst {
        // .exec files might be missing if a project has no tests. Filter in execution phase.
        executionData = executionData.filter { it.canRead() }
    }
}

fun getReportTasks(jacocoReport: JacocoReport): List<JacocoReport> {
    return getSubprojectList()
        .map {
            it.tasks.withType<JacocoReport>()
                .filter { report -> report != jacocoReport }
        }.flatten()
}

@Suppress("UnstableApiUsage")
task<JacocoReport>("jacocoFullReport") {
    dependsOn("jacocoMergeReport")
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Generates an aggregate report from all subprojects"

    val jacocoMergeTask = tasks.named("jacocoMergeReport", JacocoMerge::class).orNull
    val destFile = jacocoMergeTask?.destinationFile
    logger.lifecycle("Using aggregated file: $destFile")
    executionData.from(destFile)
    val that = this
    project.afterEvaluate {
        // The JacocoReport tasks are the source of truth for class files and sources.
        val reportTasks = getReportTasks(that)
        classDirectories.from(project.files(reportTasks.mapNotNull { it.classDirectories }))
        sourceDirectories.from(project.files(reportTasks.mapNotNull { it.sourceDirectories }))
        additionalSourceDirs.from(project.files(reportTasks.mapNotNull { it.sourceDirectories }))
    }
}
