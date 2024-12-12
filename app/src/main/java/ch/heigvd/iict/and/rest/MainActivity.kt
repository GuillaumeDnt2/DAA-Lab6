package ch.heigvd.iict.and.rest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ch.heigvd.iict.and.rest.ui.screens.AppContact
import ch.heigvd.iict.and.rest.ui.theme.MyComposeApplicationTheme

class MainActivity : ComponentActivity() {

    //TODO: Récupérer l'UUID dans les préférences depuis ici et s'il existe le passer au ViewModel
    // qui le donnera au repository, observer la valeur de l'UUID dans le ViewModel pour savoir
    // s'il change à cause de enroll et finalement le sauvegarder dans les préférences à la fin.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyComposeApplicationTheme {
                AppContact(application = application as ContactsApplication)
            }
        }

    }

}