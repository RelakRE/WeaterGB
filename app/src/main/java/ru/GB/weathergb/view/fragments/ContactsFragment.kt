package ru.GB.weathergb.view.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.item_contact.view.*
import ru.GB.weathergb.R
import ru.GB.weathergb.databinding.FragmentContactsBinding
import ru.GB.weathergb.model.contacts.Contacts

class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding: FragmentContactsBinding
        get() = _binding!!

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initContacts()
    }

    private fun initContacts() {
        Contacts(requireContext(), requireActivity()).getContacts()
    }


    private fun intentCall(phoneNumber: String) {
        if (phoneNumber.isEmpty()) return

        startActivity(Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel: $phoneNumber")
        })
    }

    fun addContacts(contacts: Map<String, String>) {
        contacts.forEach {
            binding.containerForContacts.addView(
                layoutInflater.inflate(
                    R.layout.item_contact,
                    binding.containerForContacts, false
                ).apply {
                    contact_name.text = it.key
                    contact_number.text = it.value
                    setOnClickListener { intentCall(contact_number.text.toString()) }
                }
            )
        }
    }
}