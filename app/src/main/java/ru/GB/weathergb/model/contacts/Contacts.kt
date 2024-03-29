package ru.GB.weathergb.model.contacts

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import ru.GB.weathergb.MainActivity
import ru.GB.weathergb.R
import ru.GB.weathergb.utils.Permissions
import ru.GB.weathergb.view.fragments.ContactsFragment

class Contacts(private val context: Context, private val activity: Activity) {
// TODO: сделать синглтоном, собирать в :Application, Только что с премишнами делать непонятно

    val requiredPermissions = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.CALL_PHONE
    )

    fun getContacts() {
        if (Permissions.permissionReceived(requiredPermissions, activity)) {
            val currentFragment =
                (activity as MainActivity).supportFragmentManager.findFragmentById(
                    R.id.container
                ) as Fragment
            if (currentFragment is ContactsFragment) {
                currentFragment.addContacts(Contacts(context, activity).queryContacts())
            }
        }
        Permissions.requestPermission(requiredPermissions, activity)
    }

    fun queryContacts(): Map<String, String> {

        val contacts = emptyMap<String, String>().toMutableMap()

        val contentResolved = context.contentResolver

        val phoneNumbers = getPhoneNumber(contentResolved)
        val nameNumbers = getNameNumbers(contentResolved)

        nameNumbers.forEach {
            contacts[it.value] = phoneNumbers.getOrDefault(it.key, "")
        }

        return contacts.toMap()
    }

    private fun getNameNumbers(contentResolved: ContentResolver): Map<String, String> {

        val names = emptyMap<String, String>().toMutableMap()

        val cursorContacts = contentResolved.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        )

        cursorContacts?.use { cursor ->

            val indexName = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val indexContactId = cursor.getColumnIndex(ContactsContract.Contacts._ID)

            while (cursor.moveToNext()) {
                names[cursor.getString(indexContactId)] = cursor.getString(indexName)
            }
        }

        return names
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