package com.victor_sml.playlistmaker

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.text.SimpleDateFormat
import java.util.Locale

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
    val country: String?
) : RecyclerItemType, Parcelable {
    private constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(artistName)
        parcel.writeString(trackName)
        parcel.writeString(artworkUrl100)
        parcel.writeString(trackTime)
        parcel.writeString(collectionName)
        parcel.writeString(releaseDate)
        parcel.writeString(primaryGenreName)
        parcel.writeString(country)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: Parcel): Track {
            return Track(parcel)
        }

        override fun newArray(size: Int): Array<Track?> {
            return arrayOfNulls(size)
        }
    }

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