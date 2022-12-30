package ru.moondi.rallyinfo

import android.content.Context
import android.content.SharedPreferences

class LocalDataSource {
    private val sharedPreferences: SharedPreferences = App.getSApplicationContext()
        .getSharedPreferences(NAME_APP_PREFERENCE, Context.MODE_PRIVATE)

    fun saveDataUrlResultsToSharedPreferences(urlResults: String) {
        sharedPreferences.edit().putString(URL_RESULTS_KEY, urlResults).apply()
    }

    fun getDataUrlResultsFromSharedPreferences(): String {
        return sharedPreferences.getString(URL_RESULTS_KEY, URL_RESULTS_VALUE)!!
    }

    fun getDataUrlStreamFromSharedPreferences(): String {
        return sharedPreferences.getString(URL_STREAM_KEY, URL_STREAM_VALUE)!!
    }

    fun saveDataUrlStreamToSharedPreferences(urlStream: String) {
        sharedPreferences.edit().putString(URL_STREAM_KEY, urlStream).apply()
    }

    fun getDataUrlSiteNewsFromSharedPreferences(): String {
        return sharedPreferences.getString(URL_SITE_NEWS_KEY, URL_SITE_NEWS_VALUE)!!
    }

    fun saveDataUrlSiteNewsToSharedPreferences(urlsSiteNews: String) {
        sharedPreferences.edit().putString(URL_SITE_NEWS_KEY, urlsSiteNews).apply()
    }

    fun getDataUrlResultsDakarToSharedPreferences(): String {
        return sharedPreferences.getString(URL_RESULTS_DAKAR_KEY, URL_RESULTS_DAKAR_VALUE)!!
    }

    fun saveDataUrlResultsDakarToSharedPreferences(urlResultDakar: String) {
        sharedPreferences.edit().putString(URL_RESULTS_DAKAR_KEY, urlResultDakar).apply()
    }




}