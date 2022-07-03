package win.rainchan.mirai.miraicd

import io.ktor.util.Identity.decode
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.command.ConsoleCommandSender
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.info
import net.mamoe.yamlkt.Yaml
import kotlin.concurrent.thread
import kotlin.io.path.*


object MiraiCDMain : KotlinPlugin(
    JvmPluginDescription(
        id = "win.rainchan.mirai.miraicd",
        name = "MiraiCD",
        version = "0.1.0"
    ) {
        author("RainChan")

    }
), WebHookHandler {
    private lateinit var server: WebHookServer
    private val configList = hashMapOf<String, DeployConfig>()

    @OptIn(ExperimentalCommandDescriptors::class)
    override fun onEnable() {
        logger.info { "Plugin loaded" }
        configFolder.mkdir()
        dataFolder.mkdir()
        loadConfig()
        server = WebHookServer(8000, this)
        server.start()
//        val task = DeployTask(
//            dataFolderPath,
//            MiraiConsole.INSTANCE.rootPath,
//            "MiraiCD",
//            "git@github.com:mzdluo123/MiraiCD.git",
//            "master",
//            "buildPlugin"
//        )
//        startDeployTask(task)
    }

    override fun onTag(repo: String, tag: String) {
        logger.info("onTag ${repo} ${tag}")
        val i = configList[repo] ?: return
        if (i.tag_regex.isEmpty()) {
            return
        }
        val regex = i.tag_regex.toRegex()
        if (regex.matches(tag)) {
            val task = i.toTask(dataFolderPath, MiraiConsole.INSTANCE.rootPath, repo)
            startDeployTask(task)
        }
    }

    override fun onPush(repo: String, branch: String) {
        logger.info("onPush ${repo} ${branch}")
        val i = configList[repo] ?: return
        if (i.branch.isEmpty()) {
            return
        }
        if (branch == i.branch) {
            val task = i.toTask(dataFolderPath, MiraiConsole.INSTANCE.rootPath, repo)
            startDeployTask(task)
        }
    }

    private fun loadConfig() {
        for (i in configFolderPath.listDirectoryEntries()) {
            val item = Yaml.decodeFromString(DeployConfig.serializer(), i.readText())
            configList[i.name.split(".")[0]] = item
        }
    }

    private fun startDeployTask(task: DeployTask) {
        thread {
            task.deploy()
            runBlocking {
                CommandManager.INSTANCE.executeCommand(ConsoleCommandSender, PlainText("/stop"), false)
            }
        }
    }
}
