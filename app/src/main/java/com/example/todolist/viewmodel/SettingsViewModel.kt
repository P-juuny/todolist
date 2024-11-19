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
        val savedThemeMode = preferences.getInt(KEY_THEME_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        _themeMode.value = savedThemeMode
        AppCompatDelegate.setDefaultNightMode(savedThemeMode)
    }

    fun setThemeMode(mode: Int) {
        preferences.edit().putInt(KEY_THEME_MODE, mode).apply()
        _themeMode.value = mode
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}