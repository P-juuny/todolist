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
        observeViewModel()
    }

    private fun setupThemeSelection() {
        binding.themeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val themeMode = getThemeModeFromId(checkedId)
            viewModel.setThemeMode(themeMode)
        }
    }

    private fun getThemeModeFromId(checkedId: Int): Int = when (checkedId) {
        R.id.radioLightTheme -> LIGHT_THEME
        R.id.radioDarkTheme -> DARK_THEME
        else -> SYSTEM_THEME
    }

    private fun setupTodoVisibility() {
        binding.switchTodoVisibility.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setTodoHidden(isChecked)
        }
    }

    private fun observeViewModel() {
        with(viewModel) {
            themeMode.observe(viewLifecycleOwner) { mode ->
                updateThemeRadioButton(mode)
            }

            todoHidden.observe(viewLifecycleOwner) { collapsed ->
                binding.switchTodoVisibility.isChecked = collapsed
            }
        }
    }

    private fun updateThemeRadioButton(mode: Int) {
        val radioButton = when (mode) {
            LIGHT_THEME -> binding.radioLightTheme
            DARK_THEME -> binding.radioDarkTheme
            else -> binding.radioSystemTheme
        }
        radioButton.isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}