package live.mufin

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import live.mufin.plugins.configureRouting
import live.mufin.plugins.configureSerialization
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

const val FILE_URL = "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar";
const val DEFAULT_VERSION = "1.18.1-R0.1-SNAPSHOT"
val FILE_NAME = "${System.getProperty("user.dir")}/buildtools/BuildTools.jar";
val LOGGER: Logger = LoggerFactory.getLogger("bukkitgetter")

fun main(args: Array<String>) {
    var port = 8080;

    if(args.isNotEmpty())
        port = args[0].toInt()

    embeddedServer(Netty, port = port) {
        configureRouting()
        configureSerialization()
        registerCustomRoutes()

        routing {
            install(StatusPages) {
                exception<IllegalStateException> { call, _ ->
                    call.respond(HttpStatusCode.InternalServerError)
                }
                exception<Exception> { call, _ ->
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }
        }

        File("${System.getProperty("user.dir")}/buildtools").mkdir();

        val timer = Timer()
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    downloadAndRunBuildtools();
                }
            },
            0, 1000 * 60 * 60
        )
    }.start(wait = true)
}

fun downloadAndRunBuildtools() {
    val inp: InputStream = URL(FILE_URL).openStream()
    val file = Files.copy(inp, Paths.get(FILE_NAME), StandardCopyOption.REPLACE_EXISTING)
    LOGGER.info("Buildtools downloaded.")

    val process = Runtime.getRuntime().exec("java -Xmx512M -jar ./buildtools/BuildTools.jar");
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    val output = StringBuilder()

    var line: String? = reader.readLine()
    while((line) != null) {
        output.append("$line \n")
        line = reader.readLine()
    }

    if(System.getenv("EXTRA_LOGGING").lowercase() == "true")
        print(output.toString())

}

enum class APIType {
    SERVER,
    API,
    NONE
}

fun getSpigot(type: APIType = APIType.NONE, version: String = DEFAULT_VERSION): File {
    val baseDirP = System.getProperty("user.dir");
    val baseDir = File(baseDirP);
    if(!baseDir.exists())
        throw IllegalStateException("Bukkit directory doesn't exist.")
    val file = when(type) {
        APIType.SERVER -> File("$baseDirP/Spigot/Spigot-Server/target/spigot-$version.jar")
        APIType.API -> File("$baseDirP/Spigot/Spigot-API/target/spigot-api-$version.jar")
        APIType.NONE -> File("$baseDirP/spigot-${version.split("-")[0]}.jar")
    };

    if(!file.exists())
        throw IllegalStateException("Spigot jar not found.")
    return file
}

fun Application.registerCustomRoutes() {
    routing {
        spigotRouting()
    }
}

fun Route.spigotRouting() {
    route("/spigot") {
        get {
            val version = call.parameters["version"];

            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "spigot.jar").toString()
            )

            if(version == null)
                call.respondFile(getSpigot(APIType.NONE))
            else
                call.respondFile(getSpigot(APIType.NONE, version))
        }
        get("api") {
            val version = call.parameters["version"];

            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "spigot.jar").toString()
            )

            if(version == null)
                call.respondFile(getSpigot(APIType.API))
            else
                call.respondFile(getSpigot(APIType.API, version))
        }
        get("server") {
            val version = call.parameters["version"];

            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, "spigot.jar").toString()
            )

            if(version == null)
                call.respondFile(getSpigot(APIType.SERVER))
            else
                call.respondFile(getSpigot(APIType.SERVER, version))
        }
    }
}