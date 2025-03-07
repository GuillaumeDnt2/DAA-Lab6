package ch.heigvd.iict.and.rest

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.content.edit
import ch.heigvd.iict.and.rest.ui.screens.AppContact
import ch.heigvd.iict.and.rest.ui.screens.lab.AppContactAdd
import ch.heigvd.iict.and.rest.ui.screens.lab.AppContactEdit
import ch.heigvd.iict.and.rest.ui.theme.MyComposeApplicationTheme
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModel
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModelFactory

/**
 * This is the main activity of the application.
 *
 * Authors : Dunant Guillaume, Junod Arthur, Häffner Edwin
 */
class MainActivity : ComponentActivity() {

    private lateinit var prefs: SharedPreferences
    private var fromShared = false

    private val contactVM : ContactsViewModel by viewModels {
        ContactsViewModelFactory(application as ContactsApplication)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getPreferences(Context.MODE_PRIVATE)
        val uuid = prefs.getString("uuid", null)
        if (uuid != null) {
            fromShared = true
            contactVM.getUuid().postValue(uuid)
        } else {
            contactVM.enroll()
        }


        //Observe mutableData dans le ViewModel
        contactVM.applicationStatus.observe(this) { status ->
            when (status) {
                ContactsViewModel.ApplicationStatus.EDIT -> {
                    setContent {
                        MyComposeApplicationTheme {
                            AppContactEdit(
                                application = application as ContactsApplication,
                                contactId = contactVM.idToEdit.value!!
                            )
                        }
                    }
                }
                ContactsViewModel.ApplicationStatus.ADD -> {
                    setContent {
                        MyComposeApplicationTheme {
                            AppContactAdd(application = application as ContactsApplication)
                        }
                    }
                }
                ContactsViewModel.ApplicationStatus.INITIAL -> {
                    setContent {
                        MyComposeApplicationTheme {
                            AppContact(application = application as ContactsApplication)
                        }
                    }
                }
                else -> {
                    setContent {
                        MyComposeApplicationTheme {
                            AppContact(application = application as ContactsApplication)
                        }
                    }
                }
            }
        }

        contactVM.getUuid().observe(this) { value ->
            Log.d("Repo", "UUID changed to $value")
            if (value != null) {
                prefs.edit {
                    putString("uuid", value)
                }
            }

            // Si le changement de l'uuid provient de l'initialisation depuis la sharedPreference
            if (fromShared) {
                fromShared = false
                contactVM.refresh()
            } else {
                contactVM.updateLocalDatabase()
            }

        }

    }
}