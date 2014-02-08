@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.6')
import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*


class SrtTranslater {
    private static def NEW_LINE = "\r\n"
    private static def http = new HTTPBuilder('http://translate.google.com')

    static void main(String[] args) {
        println "args:" + args
        args.each {
            process(it)
        }
    }

    private def static process(def fileName) {
        def lines = new File(fileName).readLines("utf-8")
        def result = []

        String tempLine = ""

        lines.each {
            println it

            //空行
            if (it == "") {
                result.push(translate(tempLine))
                result.push("")
                tempLine = ""
            }

            //行号
            if (it ==~ /^\d+$/) {
                result.push(it)
                return
            }

            //时间
            if (it ==~ /^\d\d:\d\d:\d\d,\d\d\d \-\-> \d\d:\d\d:\d\d,\d\d\d$/) {
                result.push(it)
                return
            }

            tempLine += tempLine == "" ? it : " " + it
        }

        //处理最后一行
        if (tempLine != "") {
            result.push(translate(tempLine))
            result.push("")
            tempLine = ""
        }

        new File(fileName).write(result.join(NEW_LINE), "utf-8")
    }

    private def static translate(def origin) {
        StringReader html = null
        def retry = 0

        while (retry < 10 && html == null) {
            try {
                html = http.get(path: '/translate_a/t', contentType: "text/plain",
                        query: [
                                client: "t",
                                sl: "en",
                                tl: "zh-CN",
                                hl: "zh-CN",
                                sc: "2",
                                ie: "UTF-8",
                                oe: "UTF-8",
                                oc: "1",
                                otf: "2",
                                ssel: "3",
                                tsel: "0",
                                q: origin
                        ])
            } catch (def exp) {
                println exp
            }
        }

        if (html == null) {
            return origin
        }

        def result = ""
        Eval.me(html.readLine().replaceAll(/,+/, ","))[0].each {
            result += it[0]
        }

        return origin + NEW_LINE + result
    }
}