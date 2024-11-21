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

    companion object {
        private const val SYSTEM_THEME = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        private const val LIGHT_THEME = AppCompatDelegate.MODE_NIGHT_NO
        private const val DARK_THEME = AppCompatDelegate.MODE_NIGHT_YES
    }

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
        setupTodoVisibility()
        observeThemeMode()
        observeTodoVisibility()
    }

    private fun setupThemeSelection() {
        binding.themeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val themeMode = when (checkedId) {
                R.id.radioLightTheme -> LIGHT_THEME
                R.id.radioDarkTheme -> DARK_THEME
                else -> SYSTEM_THEME
            }
            viewModel.setThemeMode(themeMode)
        }
    }

    private fun observeThemeMode() {
        viewModel.themeMode.observe(viewLifecycleOwner) { mode ->
            val radioButton = when (mode) {
                LIGHT_THEME -> binding.radioLightTheme
                DARK_THEME -> binding.radioDarkTheme
                else -> binding.radioSystemTheme
            }
            radioButton.isChecked = true
        }
    }

    private fun setupTodoVisibility() {
        binding.switchTodoVisibility.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setTodoHidden(isChecked)
        }
    }

    private fun observeTodoVisibility() {
        viewModel.todoHidden.observe(viewLifecycleOwner) { collapsed ->
            binding.switchTodoVisibility.isChecked = collapsed
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}