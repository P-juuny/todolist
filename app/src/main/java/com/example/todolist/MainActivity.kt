package com.example.todolist

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import com.example.todolist.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        initializeFirebase()
        setupTheme()
        setupView()

    }

    private fun initializeFirebase() {
        auth = Firebase.auth
        auth.signInAnonymously()
            .addOnCompleteListener(this) {
                val message = if (it.isSuccessful) {
                    "Authentication succeeded."
                } else {
                    "Authentication failed."
                }
                Toast.makeText(baseContext, message, Toast.LENGTH_SHORT).show() // activity는 Context 대신 baseContext 사용
            }
    }


    private fun setupTheme() {
        val preferences = getSharedPreferences("todo_settings", Context.MODE_PRIVATE)
        val themeMode = preferences.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) // 기본값은 시스템 설정
        AppCompatDelegate.setDefaultNightMode(themeMode)
        // 라이트 = 1, 다크 = 2, 시스템 = -1
    }

    private fun setupView() {
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = binding.frgNav.getFragment<NavHostFragment>().navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }



}