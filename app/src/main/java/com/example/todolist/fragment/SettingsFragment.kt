package com.example.todolist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.todolist.R
import com.example.todolist.databinding.FragmentSettingsBinding
import com.example.todolist.viewmodel.SettingsViewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupThemeSelection()
        observeThemeMode()
    }

    private fun setupThemeSelection() {
        binding.themeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val themeMode = when (checkedId) {
                R.id.radioSystemTheme -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                R.id.radioLightTheme -> AppCompatDelegate.MODE_NIGHT_NO
                R.id.radioDarkTheme -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            viewModel.setThemeMode(themeMode)
        }
    }

    private fun observeThemeMode() {
        viewModel.themeMode.observe(viewLifecycleOwner) { mode ->
            val radioButton = when (mode) {
                AppCompatDelegate.MODE_NIGHT_NO -> binding.radioLightTheme
                AppCompatDelegate.MODE_NIGHT_YES -> binding.radioDarkTheme
                else -> binding.radioSystemTheme
            }
            radioButton.isChecked = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}