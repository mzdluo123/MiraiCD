package win.rainchan.mirai.miraicd

import org.apache.commons.io.FileUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Path
import kotlin.io.path.exists


class DeployTask(val baseFolder:Path,
                 val consoleFolder:Path,
                 val repoName:String,
                 val repoUrl:String,
                 val buildTask:String) {
    private fun runCMD(cmd:List<String>,workingDir:Path){
        val builder = ProcessBuilder(
           cmd
        )
        builder.directory(workingDir.toFile())
        builder.redirectErrorStream(true)
        val p = builder.start()
        val r = BufferedReader(InputStreamReader(p.inputStream))
        var line: String?
        while (true) {
            line = r.readLine()
            if (line == null) {
                break
            }
            println(line)
        }
        println("====run finish===")
        p.waitFor()
        if ( p.exitValue() != 0){
            error("cmd execute error")
        }
    }

    fun clone(branchName:String) = runCMD(listOf("git","clone","-b",branchName,repoUrl),baseFolder)

    fun checkout(branchOrTag:String){
        runCMD(listOf("git","reset","HEAD","."),baseFolder.resolve(repoName))
        runCMD(listOf("git","fetch"),baseFolder.resolve(repoName))
        runCMD(listOf("git","checkout",branchOrTag),baseFolder.resolve(repoName))
    }

    fun pull(branch:String){
        runCMD(listOf("git","reset","HEAD","."),baseFolder.resolve(repoName))
        runCMD(listOf("git","checkout",branch),baseFolder.resolve(repoName))
        runCMD(listOf("git","pull"),baseFolder.resolve(repoName))
    }

    fun runBuild() {
        val osName = System.getProperty("os.name")
        if (osName.startsWith("Windows")) {
            runCMD(listOf("./gradlew.bat","--no-daemon",buildTask),baseFolder.resolve(repoName))
        } else {
            runCMD(listOf("chmod","+x","gradlew"),baseFolder.resolve(repoName))
            runCMD(listOf("./gradlew","--no-daemon",buildTask),baseFolder.resolve(repoName))
        }
    }

    fun rmOldPlugin(){
        FileUtils.deleteDirectory(baseFolder.resolve(repoName).resolve("build").resolve("mirai").toFile())
    }

    fun cpNewPlugin(){
        FileUtils.copyDirectory(baseFolder.resolve(repoName).resolve("build").resolve("mirai").toFile(),
            consoleFolder.resolve("plugins").toFile()
        )
    }


    fun deployBranch(branch: String){
            if (!baseFolder.resolve(repoName).exists()){
                clone(branch)
            }
            pull(branch)
            rmOldPlugin()
            runBuild()
            cpNewPlugin()
        }

    fun deployTag(tag: String){
        if (!baseFolder.resolve(repoName).exists()){
            clone(tag)
        }
        checkout(tag)
        rmOldPlugin()
        runBuild()
        cpNewPlugin()
    }

}