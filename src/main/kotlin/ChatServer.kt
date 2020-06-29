import java.net.ServerSocket
import java.net.Socket
import java.util.*

/** Implements a server to be used for the chat. accepts connections
 * from clients and assigns them to new connection handler objects
 */
class ChatServer(port: Int) {
    init {
        val server = ServerSocket(port)
        val sc = Scanner(System.`in`)
        while (true) {
            val client: Socket = server.accept()
            println("Would you like to accept client with address ${client.inetAddress}? (y/n)")
            if (sc.nextLine().trim() == "y") {
                println("Accepted from " + client.inetAddress)
                val c = ChatHandler(client)
                c.start()
            } else {
                client.close()
            }
        }
    }
}

fun main() {
    ChatServer(PORT_NUMBER)
}