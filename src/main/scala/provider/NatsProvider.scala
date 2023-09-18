package provider

import com.google.gson.Gson
import io.nats.client.support.Encoding
import io.nats.client._
import provider.data.{GSonNats, Payload}

import java.io.{File, PrintWriter}
import java.nio.charset.StandardCharsets
import java.time.Instant
import scala.util.Random


case class NatsProvider(private val accessToken: String, private val natsUrl: String, private val stream: String) {
  private var options: Options = _
  private var nc: Connection = _
  private var connectionDispatcher: Dispatcher = _

  private def init(): Unit = {
    val jwt = createAppJwt(accessToken)
    val temp = File.createTempFile("temp", null)
    temp.deleteOnExit()
    val pw = new PrintWriter(temp)

    try {
      pw.write(jwt)
    } finally {
      pw.close()
    }
    val tempPath = temp.getAbsolutePath
    val authHandler = Nats.credentials(tempPath)
    options = Options.builder().server(natsUrl).authHandler(authHandler).build()

  }

  def connect(connectionMessageHandler: MessageHandler): Unit = {
    init()
    nc = Nats.connect(options)
    connectionDispatcher = nc.createDispatcher(connectionMessageHandler)
  }

  def subscribe(subscribeMessageHandler: MessageHandler) {
    if (nc == null || connectionDispatcher == null)
      throw new Exception("Nats connection is not established")
    else
      connectionDispatcher.subscribe(stream, subscribeMessageHandler)
  }

  def unsubscribe() {
    if (nc == null || connectionDispatcher == null)
      throw new Exception("Nats connection is not established")
    else
      connectionDispatcher.unsubscribe(stream)
  }

  def publish(message: String) {
    if (nc == null)
      throw new Exception("Nats connection is not established")
    else
      nc.publish(stream, s"$message".getBytes(StandardCharsets.UTF_8))
  }


  def publish(byteArray: Array[Byte]) {
    if (nc == null)
      throw new Exception("Nats connection is not established")
    else
      nc.publish(stream, byteArray)
  }

  def disconnect() {
    nc.close()
  }

  private def createAppJwt(seed: String): String = {
    val encodedAccSeed = seed.toCharArray
    val account = NKey.fromSeed(encodedAccSeed)
    val accPubkey = account.getPublicKey
    val payload = Payload(
      jti = generateJti(),
      iat = generateIat(),
      iss = new String(accPubkey),
      name = "developer",
      sub = new String(accPubkey),
      nats = getNatsConfig
    )

    val jwt = signJwt(payload, account)
    "-----BEGIN NATS USER JWT-----\n" + jwt + "\n------END NATS USER JWT------\n\n************************* IMPORTANT *************************\nNKEY Seed printed below can be used to sign and prove identity.\nNKEYs are sensitive and should be treated as secrets. \n\n-----BEGIN USER NKEY SEED-----\n" + seed + "\n------END USER NKEY SEED------\n\n*************************************************************"

  }


  private def generateJti(): String = {
    val timestamp = Instant.now().getEpochSecond
    val random = Random.nextDouble().toString.substring(2)
    s"$timestamp$random"
  }

  private def generateIat(): Long = {
     Instant.now().getEpochSecond
  }

  private def getNatsConfig: GSonNats = {
    new Gson().fromJson("{\"pub\": {}, \"sub\": {}, \"subs\": -1, \"data\": -1, \"payload\": -1, \"_type\": \"user\", \"version\": 2}", classOf[GSonNats])


  }

  private def signJwt(payload: Payload, account: NKey): String = {
    val header = "{\"typ\": \"JWT\", \"alg\": \"ed25519-nkey\"}"
    val gson = new Gson()
    val headerEncoded = new String(Encoding.base64UrlEncode(header.getBytes), StandardCharsets.UTF_8).replaceAll("=", "");

    val payloadString = gson.toJson(payload)

    val realPayloadString = payloadString.replace("_type", "type") //Workaround for 'type' field not allowed in Scala language
    val payloadEncoded = new String(Encoding.base64UrlEncode(realPayloadString.getBytes), StandardCharsets.UTF_8).replaceAll("=", "");
    val jwtBase = headerEncoded + "." + payloadEncoded
    val signature = new String(Encoding.base64UrlEncode(account.sign(jwtBase.getBytes)), StandardCharsets.UTF_8).replaceAll("=", "");
    jwtBase + "." + signature
  }
}