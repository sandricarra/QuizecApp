/*package pt.isec.ams.quizec.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.util.Log
import java.util.*

class LanguageViewModel {

    private val PREFS_NAME = "app_preferences"
    private val LANGUAGE_KEY = "language"

    fun setAppLocale(context: Context, languageCode: String) {
        Log.d("LanguageViewModel", "Setting app locale to: $languageCode") // Log del idioma seleccionado

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        // Crea la configuración con el idioma elegido
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        // Crea un contexto con la configuración
        val localizedContext = context.createConfigurationContext(config)

        // Aplicamos la nueva configuración
        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        // Guardamos la preferencia de idioma
        saveLanguagePreference(context, languageCode)
    }

    private fun saveLanguagePreference(context: Context, languageCode: String) {
        Log.d("LanguageViewModel", "Saving language preference: $languageCode") // Log del idioma guardado

        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(LANGUAGE_KEY, languageCode)
        editor.apply()
    }

    fun getLanguagePreference(context: Context): String {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val languageCode = sharedPreferences.getString(LANGUAGE_KEY, "pt") ?: "pt"
        Log.d("LanguageViewModel", "Retrieved language preference: $languageCode") // Log del idioma recuperado
        return languageCode
    }
}
*/
package pt.isec.ams.quizec.ui.viewmodel

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import java.util.*

class LanguageViewModel {

    // Log tag para depuración
    private val TAG = "LanguageViewModel"

    // Método para establecer el idioma de la aplicación
    fun setAppLocale(context: Context, languageCode: String) {
        Log.d(TAG, "Setting app locale to: $languageCode") // Log del idioma seleccionado

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        // Crea la configuración con el idioma elegido
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        // Crea un contexto con la configuración
        val localizedContext = context.createConfigurationContext(config)

        // Aplicamos la nueva configuración
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    // Método para obtener el idioma predeterminado del sistema
    fun getSystemLanguage(): String {
        return Locale.getDefault().language // Devuelve el código del idioma del sistema (por ejemplo, "es", "en", "pt")
    }

    // Método para comprobar y establecer el idioma al iniciar la aplicación
    fun checkAndSetLanguage(context: Context) {
        val systemLanguage = getSystemLanguage()
        Log.d(TAG, "System language: $systemLanguage") // Log del idioma del sistema

        // Establece el idioma de la aplicación según el idioma del sistema
        setAppLocale(context, systemLanguage)
    }
}
