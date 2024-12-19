package ch.heigvd.iict.and.rest

import androidx.lifecycle.MutableLiveData
import ch.heigvd.iict.and.rest.database.ContactsDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class ContactsRepository(private val contactsDao: ContactsDao) {

    //TODO: avoir l'UUID afin de pouvoir l'utiliser pour communiquer avec le serveur.
    // Il faut également mettre les fontions enroll et refresh ici qui seront appelées par le ViewModel.

    val allContacts = contactsDao.getAllContactsLiveData()

    val uuid = MutableLiveData<String>();


    suspend fun enroll() : String = withContext(Dispatchers.IO){
        val url = URL("https://daa.iict.ch/enroll")
        url.readText()
    }

    suspend fun fetchAll(uuid: String) : List<> = withContext(Dispatchers.IO){
        val url = URL("https://daa.iict.ch/refresh")
        url.readText()
    }

}