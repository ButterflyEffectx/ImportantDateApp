package com.example.importantdatesreminder

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import java.util.Locale

class LocaleHelper {
    companion object {
        private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

        fun onAttach(context: Context): Context {
            val lang = getPersistedLanguage(context, Locale.getDefault().language)
            return setLocale(context, lang)
        }

        fun getLanguage(context: Context): String {
            return getPersistedLanguage(context, Locale.getDefault().language)
        }

        fun setLocale(context: Context, language: String): Context {
            persist(context, language)

            return updateResources(context, language)
        }

        private fun getPersistedLanguage(context: Context, defaultLanguage: String): String {
            val preferences = context.getSharedPreferences("language_pref", Context.MODE_PRIVATE)
            return preferences.getString(SELECTED_LANGUAGE, defaultLanguage) ?: defaultLanguage
        }

        private fun persist(context: Context, language: String) {
            val preferences = context.getSharedPreferences("language_pref", Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putString(SELECTED_LANGUAGE, language)
            editor.apply()
        }

        @Suppress("DEPRECATION")
        private fun updateResources(context: Context, language: String): Context {
            val locale = Locale(language)
            Locale.setDefault(locale)

            val resources = context.resources
            val configuration = Configuration(resources.configuration)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.setLocale(locale)
                val localeList = LocaleList(locale)
                LocaleList.setDefault(localeList)
                configuration.setLocales(localeList)
                return context.createConfigurationContext(configuration)
            } else {
                configuration.locale = locale
                resources.updateConfiguration(configuration, resources.displayMetrics)
                return context
            }
        }
    }
}