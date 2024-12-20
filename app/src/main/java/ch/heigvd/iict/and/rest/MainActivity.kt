package ch.heigvd.iict.and.rest

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
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

class MainActivity : ComponentActivity() {

    //TODO: Récupérer l'UUID dans les préférences depuis ici et s'il existe le passer au ViewModel
    // qui le donnera au repository, observer la valeur de l'UUID dans le ViewModel pour savoir
    // s'il change à cause de enroll et finalement le sauvegarder dans les préférences à la fin.

    private lateinit var prefs: SharedPreferences

    private val contactVM : ContactsViewModel by viewModels {
        ContactsViewModelFactory(application as ContactsApplication)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getPreferences(Context.MODE_PRIVATE)
        val uuid = prefs.getString("uuid", null)
        if (uuid != null) {
            contactVM.uuid.postValue(uuid)
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

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
        contactVM.uuid.observe(this) { value ->
            if (value != null) {
                prefs.edit {
                    putString("uuid", value)
                }
            }
        }
    }
}