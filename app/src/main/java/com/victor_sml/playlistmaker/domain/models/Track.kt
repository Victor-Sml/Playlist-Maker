package com.victor_sml.playlistmaker.domain.models

import android.os.Parcelable
import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.victor_sml.playlistmaker.recycler.RecyclerItemType
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Locale
@Parcelize
data class Track(
    val artistName: String?,
    val trackName: String?,
    val artworkUrl100: String?,
    @SerializedName("trackTimeMillis")
    @JsonAdapter(TrackTimeJsonAdapter::class)
    val trackTime: String?,
    val collectionName: String?,
    @JsonAdapter(ReleaseDateJsonAdapter::class)
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
) : RecyclerItemType, Parcelable {

    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")

    /**
     * Переводит миллисекунды в формат: минуты:секунды.
     */
    class TrackTimeJsonAdapter : TypeAdapter<String?>() {
        override fun write(writer: JsonWriter?, value: String?) {
            if (value == null) {
                writer?.nullValue()
                return
            }
            writer?.value(value)
        }

        override fun read(reader: JsonReader?): String? {
            if (reader?.peek() == JsonToken.NULL) {
                reader.nextNull()
                return null
            }
            if (reader?.peek() == JsonToken.NUMBER) {
                val trackTimeMillis = reader.nextLong()
                return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis)
            } else
                return reader?.nextString()
        }

    }

    /**
     * Оставляет в [releaseDate] только год.
     */
    class ReleaseDateJsonAdapter : TypeAdapter<String?>() {
        override fun write(writer: JsonWriter?, value: String?) {
            if (value == null) {
                writer?.nullValue()
                return
            }
            writer?.value(value.take(4))
        }

        override fun read(reader: JsonReader?): String? {
            if (reader?.peek() == JsonToken.NULL) {
                reader.nextNull()
                return null
            }
            return reader?.nextString()?.take(4)
        }
    }
}