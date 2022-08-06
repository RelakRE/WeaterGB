package ru.GB.weathergb.model.contacts

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import ru.GB.weathergb.utils.Permissions

class Contacts(private val context: Context, private val activity: Activity) {
// TODO: сделать синглтоном, собирать в :Application, Только что с премишнами делать непонятно

    val requiredPermissions = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.CALL_PHONE
    )

    fun getContacts(): Map<String, String> {

        Permissions.requestPermission(requiredPermissions, activity)

        return if (Permissions.permissionReceived(
                requiredPermissions,
                context
            )
        ) queryContacts() else
            emptyMap()

    }

    private fun queryContacts(): Map<String, String> {

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