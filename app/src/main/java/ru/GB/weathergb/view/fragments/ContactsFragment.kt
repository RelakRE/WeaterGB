package ru.GB.weathergb.view.fragments

import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.item_contact.view.*
import ru.GB.weathergb.R
import ru.GB.weathergb.databinding.FragmentContactsBinding

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
        getContacts()
    }

    private fun getContacts() {
        val contentResolved = requireContext().contentResolver
        var cursorContacts = contentResolved.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )
        cursorContacts?.use { cursor ->
            val indexName = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            while (cursor.moveToNext()) {
//                binding.containerForContacts.addView(TextView(requireContext()).apply {
//                    text = cursor.getString(indexName)
//                })
                binding.containerForContacts.addView(
                    layoutInflater.inflate(
                        R.layout.item_contact,
                        binding.containerForContacts, false
                    ).apply { contact_name.text = cursor.getString(indexName)}
                )
            }
        }
    }

}