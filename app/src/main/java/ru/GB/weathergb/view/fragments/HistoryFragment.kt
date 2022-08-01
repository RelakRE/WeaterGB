package ru.GB.weathergb.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.GB.weathergb.databinding.FragmentHistoryBinding
import ru.GB.weathergb.domain.Weather
import ru.GB.weathergb.view.adapters.HistoryAdapter
import ru.GB.weathergb.viewmodel.BasicAppState
import ru.GB.weathergb.viewmodel.HistoryViewModel

class HistoryFragment : Fragment() {

    private val historyViewModel: HistoryViewModel by viewModels()
    private var _binding: FragmentHistoryBinding? = null
    private val binding: FragmentHistoryBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViewModel()
        historyViewModel.fetch()
    }

    private fun initializeViewModel() {
        historyViewModel.getLiveData().observe(viewLifecycleOwner) {
            onChangeHistoryViewModel(it)
        }
    }

    private fun onChangeHistoryViewModel(state: BasicAppState) {
        when (state) {
            is BasicAppState.Success<*> -> showHistory(state)
            else -> {}
        }
    }

    private fun showHistory(state: BasicAppState.Success<*>) {
        val list = state.data as List<Weather>
        binding.recyclerHistory.apply {
            adapter = HistoryAdapter(list)
            layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}