import com.google.gson.Gson
import java.io.*
import java.net.Socket
import java.net.UnknownHostException
import java.util.*


object SocketTest {
    const val hostIp = HOST_IP_ADDRESS
    const val hostPort = PORT_NUMBER
    val parser = Gson()

    fun startSender() {
        object : Thread() {
            override fun run() {
                try {
                    val socket = Socket(hostIp, hostPort)
                    val outputStream = DataOutputStream(socket.getOutputStream())
                    val sc = Scanner(System.`in`)
                    while (true) {
                        sc.nextLine().let {
                            val msg = EncryptionService.encrypt(
                                parser.toJson(
                                    MyMessage(USER_ID, it)
                                ),
                                EncryptionService.publicKey
                            )
                            println(String(msg))

                            outputStream.writeInt(msg.size)
                            outputStream.write(msg)
                        }
                        outputStream.flush()
                        sleep(200)
                    }
                } catch (e: UnknownHostException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    fun startReader() {
        object : Thread() {
            override fun run() {
                var msgLength: Int
                try {
                    val socket = Socket(hostIp, hostPort)
                    val inputStream = DataInputStream(socket.getInputStream())
                    while (true) {
                        msgLength = inputStream.readInt()
                        if (msgLength > 0) {
                            val buffer = ByteArray(msgLength)
                            inputStream.readFully(buffer)
                            println(
                                EncryptionService.decrypt(buffer)
                            )
                        }
                        sleep(200)
                    }
                } catch (e: UnknownHostException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

}

data class MyMessage(
    val sender: String,
    val content: String
)

fun main() {
    SocketTest.startSender()
    SocketTest.startReader()
}