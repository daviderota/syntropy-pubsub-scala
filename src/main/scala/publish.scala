import io.nats.client.{Message, MessageHandler}
import provider.NatsProvider

import java.nio.charset.StandardCharsets

object publish {
  def main(args: Array[String]): Unit = {
    println("Hello world!")
    val accessToken = "access_token"
    val natsUrl = "nats://url.com"
    val streamName = "stream_name"
    val nc = NatsProvider(accessToken, natsUrl, streamName)

    val connectionMessageHandler: MessageHandler = new MessageHandler() {

      override def onMessage(msg: Message): Unit = {
        val connectionResponse = new String(msg.getData, StandardCharsets.UTF_8)
        println(s"Connection Message: $connectionResponse")
      }
    }
    nc.connect(connectionMessageHandler)

    var i = 0
    for (i <- 0 to 1000) {
      println("Published msg: " + i.toString)
      nc.publish(i.toString)
      Thread.sleep(1000)
    }

  }
}