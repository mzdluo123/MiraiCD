package win.rainchan.mirai.miraicd

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object PlConfig: AutoSavePluginConfig("config") {
    val port by value<Int>(5412)
}