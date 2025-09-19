package com.example.makeitso.model.service.impl

import com.example.makeitso.model.service.ConfigurationService
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import javax.inject.Inject
import kotlinx.coroutines.tasks.await
import org.json.JSONArray

class ConfigurationServiceImpl @Inject constructor(
  private val remoteConfig: FirebaseRemoteConfig
) : ConfigurationService {

  init {
    val settings = remoteConfigSettings {
      minimumFetchIntervalInSeconds = 3600 // en dev puedes usar 0
    }
    remoteConfig.setConfigSettingsAsync(settings)
    remoteConfig.setDefaultsAsync(
      mapOf(ConfigurationService.TASK_OPTIONS to ConfigurationService.TASK_OPTIONS_DEFAULT)
    )
  }

  override suspend fun fetch() {
    remoteConfig.fetchAndActivate().await()
  }

  override fun getTaskOptions(): List<String> {
    val raw = remoteConfig.getString(ConfigurationService.TASK_OPTIONS)
    return try {
      val arr = JSONArray(raw)
      List(arr.length()) { i -> arr.getString(i) }
    } catch (e: Exception) {
      // fallback a defaults si el JSON estuviera mal
      val arr = JSONArray(ConfigurationService.TASK_OPTIONS_DEFAULT)
      List(arr.length()) { i -> arr.getString(i) }
    }
  }
}
