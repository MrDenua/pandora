package tech.linjiang.pandora

import tech.linjiang.pandora.cache.Summary
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

/**
 * <pre>
 * author : ZH-301-001
 * e-mail : denua@foxmail.com
 * time   : 2019/10/4
 * desc   :
 * </pre>
 */
object Template {

    fun getPlainText(text: String): String {
        return """ 
            {
                "msgtype": "text", 
                "text": {
                    "content": "$text"
                }, 
                "at": {
                    "atMobiles": [], 
                    "isAtAll": false
                }
            }
        """.trimIndent()
    }

    fun getMarkdown(summary: Summary): String {

        var body = summary.jsonBody
        if (body == null) {
            body = "*Empty*"
        }
        body = body.replace("\"", "'")

        var markDown = """
### **${summary.method} ${summary.url} ${summary.code}**

- status: ${summary.code}
- url: ${summary.host + summary.url}
- time: ${Date(summary.start_time)}
- from: ${getHostIp()}
${if (summary.query.isNullOrBlank()) "" else "- query" + summary.query}

### Request Body
> $body
""".trimIndent()

        markDown = markDown.replace("\n", "\\n")
        return """
            {
                 "msgtype": "markdown",
                 "markdown": {
                     "title":"${summary.code} ${summary.method} ${summary.url}",
                     "text": "$markDown"},
                "at": {
                    "atMobiles": [
                        "1567858341"
                    ], 
                    "isAtAll": false
                }
             }
             
        """.trimIndent()
    }

    private fun getHostIp(): String? {
        try {
            val allNetInterfaces = NetworkInterface.getNetworkInterfaces()
            while (allNetInterfaces.hasMoreElements()) {
                val netInterface = allNetInterfaces.nextElement() as NetworkInterface
                val addresses = netInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val ip = addresses.nextElement() as InetAddress
                    if (ip is Inet4Address && !ip.isLoopbackAddress
                            && ip.hostAddress.indexOf(":") == -1) {
                        return ip.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}