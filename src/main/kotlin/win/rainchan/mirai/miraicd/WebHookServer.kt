package win.rainchan.mirai.miraicd

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import io.ktor.utils.io.core.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.InetSocketAddress
import kotlin.concurrent.thread
import kotlin.text.toByteArray

class WebHookServer(port:Int, private val handler: WebHookHandler, path:String = "/webhook"):HttpHandler {
    private var httpServer :HttpServer
    init {
        httpServer = HttpServer.create(InetSocketAddress(port),0)
        httpServer.createContext(path,this)
    }

    fun start(){
           httpServer.start()
    }

    private fun processReq(exchange: HttpExchange){
        val headers = exchange.requestHeaders["X-GitHub-Event"]?.first() ?:return
        try {
            val content = Json.parseToJsonElement(exchange.requestBody.readBytes().decodeToString())
//            println(content)
            val repository =  content.jsonObject["repository"] ?: return
            val full_name = repository.jsonObject["name"]?.jsonPrimitive?.content ?: return
            val ref = content.jsonObject["ref"]?.jsonPrimitive?.content?.split("/") ?: return
           if (headers ==  "push"){
                if (ref[1] == "tags"){
                    handler.onTag(full_name,ref[2])
                }
                if (ref[1] == "heads"){
                    handler.onPush(full_name,ref[2])
                }
            }
        } catch (e: SerializationException){
            println(e.toString())
        }
    }


    override fun handle(exchange: HttpExchange) {
        processReq(exchange)
        val rsp = "ok".toByteArray()
        exchange.sendResponseHeaders(200, rsp.size.toLong())
        exchange.responseBody.write(rsp)
        exchange.responseBody.close()
    }
}