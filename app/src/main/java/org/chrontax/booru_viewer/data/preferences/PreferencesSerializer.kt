package org.chrontax.booru_viewer.data.preferences

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import org.chrontax.booru_viewer.data.preferences.proto.Preferences
import java.io.InputStream
import java.io.OutputStream

object PreferencesSerializer : Serializer<Preferences> {
    override val defaultValue: Preferences = Preferences.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Preferences {
        try {
            return Preferences.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: Preferences, output: OutputStream) {
        t.writeTo(output)
    }
}

val Context.preferencesStore by dataStore(
    fileName = "preferences.proto",
    serializer = PreferencesSerializer
)
