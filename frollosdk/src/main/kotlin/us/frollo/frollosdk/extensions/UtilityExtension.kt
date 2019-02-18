package us.frollo.frollosdk.extensions

import android.content.Intent
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import okhttp3.Response
import us.frollo.frollosdk.FrolloSDK
import us.frollo.frollosdk.core.ARGUMENT.ARG_DATA
import java.nio.charset.Charset
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/* Kotlin extensions */
/**
 * Checks if [value1] and [value2] are not null and executes a function after.
 * Think of this as a 2 parameters `value?.let { ... }`
 */
internal fun <T1, T2> ifNotNull(value1: T1?, value2: T2?, bothNotNull: (T1, T2) -> (Unit)) {
    if (value1 != null && value2 != null) {
        bothNotNull(value1, value2)
    }
}

/* Gson extensions */
/**
 * Converts a [json] to a given [T] object.
 * @return the converted object.
 */
internal inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)

/**
 * Retrieves the value from the [SerializedName] annotation, if present
 */
internal fun Enum<*>.serializedName(): String? {
    return javaClass.getField(name).annotations
            .filter { it.annotationClass == SerializedName::class }
            .map { it as SerializedName }
            .firstOrNull()?.value
}

/* Network Extensions */
internal val Response.clonedBodyString : String?
    get() {
        val source = this.body()?.source()
        source?.request(Long.MAX_VALUE) // Request the entire body.
        val buffer = source?.buffer()

        // Clone buffer before reading from it as if this is called second time from elsewhere,
        // the network stream has already been consumed and is no longer available and results
        // in IllegalStateException: closed
        return buffer?.clone()?.readString(Charset.forName("UTF-8"))
    }

internal fun notify(action: String, bundleExtras: Bundle? = null) {
    val broadcastManager = LocalBroadcastManager.getInstance(FrolloSDK.app)
    val intent = Intent(action)
    bundleExtras?.let { intent.putExtra(ARG_DATA, bundleExtras) }
    broadcastManager.sendBroadcast(intent)
}

internal fun Boolean.toInt() = if (this) 1 else 0

internal fun String.regexValidate(regex: String): Boolean {
    return try {
        Pattern.compile(regex).matcher(this).matches()
    } catch (e: PatternSyntaxException) {
        false
    }
}