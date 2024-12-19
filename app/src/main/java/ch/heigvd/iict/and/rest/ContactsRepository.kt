package ch.heigvd.iict.and.rest

import androidx.lifecycle.MutableLiveData
import ch.heigvd.iict.and.rest.database.ContactsDao
import ch.heigvd.iict.and.rest.models.ContactDTO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class ContactsRepository(private val contactsDao: ContactsDao) {

    //TODO: avoir l'UUID afin de pouvoir l'utiliser pour communiquer avec le serveur.
    // Il faut également mettre les fontions enroll et refresh ici qui seront appelées par le ViewModel.

    val allContacts = contactsDao.getAllContactsLiveData()

    fun getContactById(id: Long) = contactsDao.getContactByIdLiveData(id)

    val uuid = MutableLiveData<String>()

    fun deleteLocal() {
        contactsDao.clearAllContacts()
    }



    suspend fun enroll() = withContext(Dispatchers.IO){
        val url = URL("https://daa.iict.ch/enroll")
        uuid.postValue(url.readText())
    }

    private fun setUuid(url: URL) : HttpURLConnection{
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("X-UUID", uuid.value)
        return connection
    }


    suspend fun fetchAll() : List<ContactDTO> = withContext(Dispatchers.IO){
        val url = URL("https://daa.iict.ch/contacts")
        val connection = setUuid(url)
        val json = connection.url.readText(Charsets.UTF_8)
        val type = object : TypeToken<List<ContactDTO>>() {}.type
        Gson().fromJson<List<ContactDTO>>(json, type)
    }

    suspend fun fetchContact(id: String) : ContactDTO = withContext(Dispatchers.IO){
        val url = URL("https://daa.iict.ch/contacts/$id")
        val connection = setUuid(url)
        val json = connection.url.readText(Charsets.UTF_8)
        val type = object : TypeToken<ContactDTO>() {}.type
        Gson().fromJson<ContactDTO>(json, type)
    }

}