
import java.io.*
import java.net.Socket

class ChatHandler(private val socket: Socket) : Thread() {
    private val inputStream: DataInputStream = DataInputStream(BufferedInputStream(socket.getInputStream()))
    private val outputStream = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
    var requestStop = false

    override fun run() {
        var length = 0;
        try {
            handlers.add(this)
            while (true) {
                length = inputStream.readInt()
                if (length > 0) {
                    val msg = ByteArray(length)
                    inputStream.readFully(msg)
                    broadcast(msg)
                }
                sleep(50)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        } finally {
            handlers.remove(this)
            try {
                socket.close()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
    }

    companion object {
        private var handlers: MutableList<ChatHandler> = mutableListOf()

        private fun broadcast(message: ByteArray) {
            synchronized(handlers) {
                handlers.forEach {
                    try {
                        synchronized(it.outputStream) {
                            it.outputStream.writeInt(message.size)
                            it.outputStream.write(message)
                        }
                        it.outputStream.flush()
                    } catch (ex: IOException) {
                        it.requestStop = true
                    }
                }
            }
        }
    }

}

