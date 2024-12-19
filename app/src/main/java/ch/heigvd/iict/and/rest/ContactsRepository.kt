package ch.heigvd.iict.and.rest

import androidx.lifecycle.MutableLiveData
import ch.heigvd.iict.and.rest.database.ContactsDao
import java.net.URL
import kotlin.concurrent.thread

class ContactsRepository(private val contactsDao: ContactsDao) {

    //TODO: avoir l'UUID afin de pouvoir l'utiliser pour communiquer avec le serveur.
    // Il faut également mettre les fontions enroll et refresh ici qui seront appelées par le ViewModel.

    val allContacts = contactsDao.getAllContactsLiveData()

    val uuid = MutableLiveData<String>()

}