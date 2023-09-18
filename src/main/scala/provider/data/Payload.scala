package provider.data

import com.google.gson.annotations.{Expose, SerializedName}
/*
* val payload = Map(
  "jti" -> generateJti(),
  "iat" -> generateIat(),
  "iss" -> new String(accPubkey),
  "name" -> "developer",
  "sub" -> new String(accPubkey),
  "nats" -> getNatsConfig()
)*/
case class Payload(
                    @SerializedName("jti")
                    @Expose var jti: String,
                    @SerializedName("iat")
                    @Expose var iat: Long,
                    @SerializedName("iss")
                    @Expose var iss: String,
                    @SerializedName("name")
                    @Expose var name: String = "developer",
                    @SerializedName("sub")
                    @Expose var sub: String,
                    @SerializedName("nats")
                    @Expose var nats: GSonNats,

                  )
