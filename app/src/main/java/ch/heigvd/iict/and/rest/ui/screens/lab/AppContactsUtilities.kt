package ch.heigvd.iict.and.rest.ui.screens.lab

import android.content.Context
import android.widget.Toast
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.PhoneType
import ch.heigvd.iict.and.rest.models.Status
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AppContactsUtilities {

    fun handleSaveOrUpdateClick(
        context: Context,
        name: String,
        firstname: String,
        email: String,
        birthday: String,
        address: String,
        zip: String,
        city: String,
        phoneNumber: String,
        selectedPhoneType: String,
        contactsViewModel: ContactsViewModel,
        update: Boolean,
        remoteId: Long? = null,
        id: Long? = null
    ) {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val parsedBirthday: Calendar? = try {
            Calendar.getInstance().apply {
                time = dateFormat.parse(birthday)!!
            }
        } catch (e: Exception) {
            null // set birthday to null in case of error
        }

        if (parsedBirthday == null) {
            Toast.makeText(
                context,
                "Invalid birthday format. Please use dd.MM.yyyy.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }



        val newContact = Contact(
            id = id,
            name = name,
            firstname = firstname,
            email = email,
            birthday = parsedBirthday,
            address = address,
            zip = zip,
            city = city,
            type = PhoneType.valueOf(selectedPhoneType.uppercase()),
            phoneNumber = phoneNumber,
            remoteId = remoteId,
            status = if (update) {
                Status.MOD
            } else {
                Status.NEW
            }
        )

        if (update) {
            contactsViewModel.update(newContact)
            Toast.makeText(context, "Contact updated!", Toast.LENGTH_SHORT).show()
        } else {
            contactsViewModel.new(newContact)
            Toast.makeText(context, "Contact saved!", Toast.LENGTH_SHORT).show()
        }

        contactsViewModel.setApplicationStatus(ContactsViewModel.ApplicationStatus.INITIAL)
    }

}