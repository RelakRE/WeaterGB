package ru.GB.weathergb.utils

import android.Manifest
import android.Manifest.permission.READ_CONTACTS
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object Permissions {

    private const val REQUEST_CODE = 42
    private val contactsPermission = listOf(READ_CONTACTS, Manifest.permission.CALL_PHONE)

    fun requestPermission(
        permissions: Array<String>,
        activity: Activity
    ) {

        val noPermission = permissions.filter {
            ContextCompat.checkSelfPermission(
                activity,
                it
            ) != PackageManager.PERMISSION_GRANTED
        }

        if (noPermission.isNotEmpty()) {

            val shouldShowContacts = сontactsPermissionsInTheList(noPermission) &&
                    noPermission.any {
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            activity,
                            it
                        )
                    }

            if (shouldShowContacts) {
                AlertDialog.Builder(activity)
                    .setTitle("Доступ к контактам")
                    .setMessage("Доступ нужен для работы контактов")
                    .setPositiveButton(
                        "Ок"
                    ) { dialog, _ ->
                        requestPermissionShow(activity, noPermission)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Нет") { dialog, _ ->
                        dialog.dismiss()
                    }.create().show()
            } else {
                requestPermissionShow(activity, noPermission)
            }
        }
    }

    private fun сontactsPermissionsInTheList(listPermissions: List<String>): Boolean {
        return listPermissions.any {
            contactsPermission.contains(it)
        }
    }

    private fun requestPermissionShow(
        activity: Activity,
        noPermission: List<String>
    ) {
        ActivityCompat.requestPermissions(
            activity,
            noPermission.toTypedArray(),
            REQUEST_CODE
        )
    }

    fun permissionReceived(permissions: Array<String>, context: Context): Boolean {
        return permissions.none {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) != PackageManager.PERMISSION_GRANTED
        }
    }


}