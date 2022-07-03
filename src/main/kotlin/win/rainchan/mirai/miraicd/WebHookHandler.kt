package win.rainchan.mirai.miraicd

interface WebHookHandler {
    fun onTag(repo:String,tag:String)

    fun onPush(repo: String,branch: String)
}