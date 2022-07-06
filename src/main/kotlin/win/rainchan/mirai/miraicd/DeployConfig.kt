package win.rainchan.mirai.miraicd

import java.nio.file.*

@kotlinx.serialization.Serializable
data class DeployConfig(
    val repo_url: String,
    val tag_regex: String = "",
    val branch: String = "master",
    val build_task: String = "buildPlugin"
) {
    fun toTask(
        baseFolder: Path,
        consoleFolder: Path,
        repoName: String
    ) =
        DeployTask(
            baseFolder,
            consoleFolder, repoName, repo_url, build_task
        )


}
