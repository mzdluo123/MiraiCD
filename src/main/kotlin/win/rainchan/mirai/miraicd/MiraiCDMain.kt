package win.rainchan.mirai.miraicd

import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info


object MiraiCDMain : KotlinPlugin(
    JvmPluginDescription(
        id = "win.rainchan.mirai.miraicd",
        name = "MiraiCD",
        version = "0.1.0"
    ) {
        author("RainChan")

    }
),WebHookHandler {
    private lateinit var server: WebHookServer
    override fun onEnable() {
        logger.info { "Plugin loaded" }
        configFolder.mkdir()
        dataFolder.mkdir()
        server = WebHookServer(8000,this)
        server.start()
        val task = DeployTask(dataFolderPath,MiraiConsole.INSTANCE.rootPath,"MiraiCD","git@github.com:mzdluo123/MiraiCD.git","master","buildPlugin")
        task.deploy()

    }

    override fun onTag(repo: String, tag: String) {
       logger.info("onTag ${repo} ${tag}")
    }

    override fun onPush(repo: String, branch: String) {
        logger.info("onPush ${repo} ${branch}")
    }
}
