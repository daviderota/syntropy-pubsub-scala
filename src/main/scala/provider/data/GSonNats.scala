package provider.data

import com.google.gson.annotations.{Expose, SerializedName}

case class GSonNats(
                     @SerializedName("pub")
                     @Expose var pub: Object = null,
                     @SerializedName("sub")
                     @Expose var sub: Object = null,
                     @SerializedName("subs")
                     @Expose var subs: Integer = null,
                     @SerializedName("data")
                     @Expose var data: Integer = null,
                     @SerializedName("payload")
                     @Expose var payload: Integer = null,
                     @SerializedName("_type")
                     @Expose val _type: String = null,
                     @SerializedName("version")
                     @Expose var version: Integer = null
                   )
