package ru.GB.weathergb.view.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import ru.GB.weathergb.databinding.FragmentFilmDetailsBinding

class DetailsFilmFragment : Fragment() {

    private var _binding: FragmentFilmDetailsBinding? = null
    private val binding: FragmentFilmDetailsBinding
        get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmDetailsBinding.inflate(inflater)
        return binding.root
    }

}