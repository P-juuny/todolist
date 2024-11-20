package com.example.todolist.viewmodel

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val PREFS_NAME = "todo_settings"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_TODO_HIDDEN = "todo_hidden"

        private const val THEME_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    private val preferences = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    // Theme mode: 0 = 시스템 설정, 1 = 라이트 모드, 2 = 다크 모드
    private val _themeMode = MutableLiveData<Int>()
    val themeMode: LiveData<Int> = _themeMode

    private val _todoHidden = MutableLiveData<Boolean>()
    val todoHidden: LiveData<Boolean> = _todoHidden



    init {
        // 저장된 테마 설정 불러오기
        loadThemeMode()
        loadTodoHidden()
    }

    private fun loadThemeMode() {
        val savedThemeMode = preferences.getInt(KEY_THEME_MODE, THEME_SYSTEM)
        _themeMode.value = savedThemeMode
        AppCompatDelegate.setDefaultNightMode(savedThemeMode)
    }

    fun setThemeMode(mode: Int) {
        preferences.edit().putInt(KEY_THEME_MODE, mode).apply()
        _themeMode.value = mode
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun loadTodoHidden() {
        val savedTodoHidden = preferences.getBoolean(KEY_TODO_HIDDEN, false)
        _todoHidden.value = savedTodoHidden
    }

    fun setTodoHidden(hidden: Boolean) {
        preferences.edit().putBoolean(KEY_TODO_HIDDEN, hidden).apply()
        _todoHidden.value = hidden
    }

}