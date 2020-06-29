import java.math.BigInteger
import java.util.*


object EncryptionService {

    private val privateKey: BigInteger

    private const val bitlength = 1024
    private val r = Random()
    private val p = BigInteger.probablePrime(bitlength, r)
    private val q = BigInteger.probablePrime(bitlength, r)
    private val N = p.multiply(q)
    private val phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)) // (p-1)(q-1)

    val publicKey = BigInteger.probablePrime(bitlength / 2, r)

    init {
        while (phi.gcd(publicKey) > BigInteger.ONE && publicKey < phi) {
            publicKey.add(BigInteger.ONE)
        }
        privateKey = publicKey.modInverse(phi)
    }

    fun bytesToString(bytes: ByteArray): String =
        bytes.joinToString { byte ->
            byte.toString()
        }

    // Encrypt message
    fun encrypt(message: String, receiverPublicKey: BigInteger): ByteArray =
        BigInteger(message.toByteArray())
            .modPow(receiverPublicKey, N)
            .toByteArray()


    // Decrypt message
    internal fun decrypt(message: ByteArray): String = String(
        BigInteger(message)
            .modPow(privateKey, N)
            .toByteArray()
    )
}

fun main() {
    val rsa = EncryptionService
    val testString: String
    val sc = Scanner(System.`in`)

    println("Enter the plain text:")
    testString = sc.nextLine()
    println("Encrypting String: $testString")
    println(
        "String in Bytes: "
                + rsa.bytesToString(testString.toByteArray())
    )
    val encrypted: ByteArray = rsa.encrypt(testString, rsa.publicKey)
    val decrypted: String = rsa.decrypt(encrypted)

    println("Decrypted String: " + decrypted)
}