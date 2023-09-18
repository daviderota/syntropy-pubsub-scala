import io.nats.client.{Message, MessageHandler}
import provider.NatsProvider

import java.nio.charset.StandardCharsets


object subscribe_by_seed {
  def main(args: Array[String]): Unit = {
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

    val subscribeHandler = new MessageHandler() {

      override def onMessage(msg: Message): Unit = {

        val connectionResponse = new String(msg.getData, StandardCharsets.UTF_8)
        println("Message received: " + connectionResponse)
      }
    }
    nc.subscribe(subscribeHandler)
  }
}