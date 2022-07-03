package win.rainchan.mirai.miraicd

data class DeployConfig(
    val repo_url:String?,
    val tag_regex:String?,
    val branch_regex:String?,
    val build_task:String = "buildPlugin"
)
