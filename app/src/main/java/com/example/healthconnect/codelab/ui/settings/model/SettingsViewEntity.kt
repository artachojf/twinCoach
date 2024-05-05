package com.example.healthconnect.codelab.ui.settings.model

sealed class SettingsViewEntity {
    class DangerousSetting(val text: String, val action: () -> Unit) : SettingsViewEntity()
    class RegularSetting(val text: String, val action: () -> Unit) : SettingsViewEntity()
}