package com.example.todolist.viewmodel

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val PREFS_NAME = "todo_settings"
        private const val KEY_THEME_MODE = "theme_mode"
    }

    private val preferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Theme mode: 0 = 시스템 설정, 1 = 라이트 모드, 2 = 다크 모드
    private val _themeMode = MutableLiveData<Int>()
    val themeMode: LiveData<Int> = _themeMode

    init {
        // 저장된 테마 설정 불러오기
        loadThemeMode()
    }

    private fun loadThemeMode() {
        // SharedPreferences에서 테마 모드 불러오기 (기본값: 시스템 설정)
        val savedThemeMode = preferences.getInt(KEY_THEME_MODE, 0)
        _themeMode.value = savedThemeMode
        applyTheme(savedThemeMode)
    }

    fun setThemeMode(mode: Int) {
        viewModelScope.launch {
            // SharedPreferences에 테마 모드 저장
            preferences.edit().putInt(KEY_THEME_MODE, mode).apply()
            _themeMode.value = mode
            applyTheme(mode)
        }
    }

    private fun applyTheme(mode: Int) {
        val nightMode = when (mode) {
            0 -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            1 -> AppCompatDelegate.MODE_NIGHT_NO
            2 -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}