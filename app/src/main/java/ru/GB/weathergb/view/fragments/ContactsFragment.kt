package ru.GB.weathergb.view.fragments

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val cursorContacts = contentResolved.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )

        val phoneNumbers = getPhoneNumber(contentResolved)
        cursorContacts?.use { cursor ->
            val indexName = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val indexContactId = cursor.getColumnIndex(ContactsContract.Contacts._ID)

            while (cursor.moveToNext()) {
                binding.containerForContacts.addView(
                    layoutInflater.inflate(
                        R.layout.item_contact,
                        binding.containerForContacts, false
                    ).apply {
                        contact_name.text = cursor.getString(indexName)
                        contact_number.text =
                            phoneNumbers.getOrDefault(cursor.getString(indexContactId), "")
                        setOnClickListener { intentCall(contact_number.text.toString()) }
                    }
                )
            }
        }
    }

    private fun intentCall(phoneNumber: String) {
        if (phoneNumber.isEmpty()) return

        startActivity(Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel: $phoneNumber")
        })
    }

    private fun getPhoneNumber(contentResolved: ContentResolver): Map<String, String> {

        val numbers = emptyMap<String, String>().toMutableMap()

        val cursorNumber = contentResolved.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        cursorNumber?.use { cursor ->

            val indexContactID =
                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
            val indexNumber =
                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (cursor.moveToNext()) {
                numbers[cursor.getString(indexContactID)] = cursor.getString(indexNumber)
            }
        }

        return numbers.toMap()
    }
}