package ru.GB.weathergb.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object Permissions {

    val REQUEST_CODE = 42

    fun checkPermission(
        Permissions: Array<String>,
        activity: Activity
    ) {

        val noPermission = Permissions.filter {
            ContextCompat.checkSelfPermission(
                activity,
                it
            ) != PackageManager.PERMISSION_GRANTED
        }

//            Ознакомился с тем, как работает, решил упростить
//            ActivityCompat.shouldShowRequestPermissionRationale(
//                activity,
//                Manifest.permission.READ_CONTACTS
//            ) -> {
//                AlertDialog.Builder(activity)
//                    .setTitle("Доступ к контактам")
//                    .setMessage("Контакты нужны для ...")
//                    .setPositiveButton(
//                        "Разрешить"
//                    ) { _, _ ->
//                        showSnack(activity, parent, "Доступ дапли")
//                        requestPermission()
//                    }
//                    .setNegativeButton("Нет") { dialog, _ ->
//                        showSnack(activity, parent, "Доступ не хотят давать")
//                        dialog.dismiss()
//                    }.create().show()
//            }

        if (noPermission.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                noPermission.toTypedArray(),
                REQUEST_CODE
            )
        }
    }
}