package ch.heigvd.iict.and.rest

import androidx.lifecycle.MutableLiveData
import ch.heigvd.iict.and.rest.database.ContactsDao
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.ContactDTO
import ch.heigvd.iict.and.rest.models.Status
import ch.heigvd.iict.and.rest.models.toContactDTO
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

    suspend fun new(contact: Contact) = withContext(Dispatchers.IO){
        contactsDao.insert(contact)
        val res = createContact(contact.toContactDTO())
        if (res != null){
            contact.remoteId = res.id
            okStatus(contact)
        }
    }

    suspend fun update(contact: Contact) = withContext(Dispatchers.IO){
        contactsDao.update(contact)
        val res = updateContact(contact.toContactDTO())
        if (res != null){
            okStatus(contact)
        }
    }

    suspend fun delete(contact: Contact) = withContext(Dispatchers.IO){
        contact.status = Status.DEL
        contactsDao.update(contact)
        val res = deleteContact(contact.remoteId.toString())
        if (res) {
            deleteConfirmed(contact)
        }
    }

    private suspend fun deleteConfirmed(contact : Contact) = withContext(Dispatchers.IO){
        contactsDao.delete(contact)
    }

    suspend fun refresh() = withContext(Dispatchers.IO){
        if(allContacts.isInitialized){
            for(contact in allContacts.value!!){
                when (contact.status) {
                    Status.OK -> continue
                    Status.DEL -> {
                        val res = deleteContact(contact.remoteId.toString())
                        if (res) {
                            deleteConfirmed(contact)
                        }
                    }
                    Status.MOD -> {
                        val res = updateContact(contact.toContactDTO())
                        if (res != null){
                            okStatus(contact)
                        }
                    }
                    Status.NEW -> {
                        val res = createContact(contact.toContactDTO())
                        if (res != null){
                            contact.remoteId = res.id
                            okStatus(contact)
                        }
                    }
                    else -> {
                        error("Unexpected status from contact with id ${contact.id}")
                    }
                }
            }
        }
    }

    suspend fun enroll() = withContext(Dispatchers.IO){
        val url = URL("https://daa.iict.ch/enroll")
        uuid.postValue(url.readText())
    }

    private fun okStatus(contact : Contact){
        contact.status = Status.OK
        contactsDao.update(contact)
    }

    private fun setUuid(url: URL) : HttpURLConnection{
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("X-UUID", uuid.value)
        return connection
    }

    private fun checkStatus(connection: HttpURLConnection) : Boolean {
        return connection.responseCode >= 200 || connection.responseCode < 300
    }


    suspend fun fetchAll() : List<ContactDTO>? = withContext(Dispatchers.IO){
        val url = URL("https://daa.iict.ch/contacts")
        val connection = setUuid(url)
        val json = connection.url.readText(Charsets.UTF_8)

        if(checkStatus(connection)) {
            return@withContext null
        }

        val type = object : TypeToken<List<ContactDTO>>() {}.type
        Gson().fromJson<List<ContactDTO>>(json, type)
    }

    suspend fun fetchContact(id: String) : ContactDTO? = withContext(Dispatchers.IO){
        val url = URL("https://daa.iict.ch/contacts/$id")
        val connection = setUuid(url)
        val json = connection.url.readText(Charsets.UTF_8)

        if(checkStatus(connection)) {
            return@withContext null
        }

        val type = object : TypeToken<ContactDTO>() {}.type
        Gson().fromJson<ContactDTO>(json, type)
    }

    private suspend fun createContact(contact: ContactDTO) : ContactDTO? = withContext(Dispatchers.IO){
        // Set l'en-tête
        val url = URL("https://daa.iict.ch/contacts")
        val connection = setUuid(url)
        connection.setRequestProperty("Content-Type", "application/json")
        connection.requestMethod = "POST"

        // Ajoute le body
        val os = connection.outputStream
        os.write(Gson().toJson(contact).toByteArray())
        os.close()

        // Récupère la réponse
        val json = connection.url.readText(Charsets.UTF_8)

        // Vérifie que la réponse est OK
        if(!checkStatus(connection)) {
            return@withContext null
        }

        // Converti le JSON en objet et le retourne
        val type = object : TypeToken<ContactDTO>() {}.type
        Gson().fromJson<ContactDTO>(json, type)

    }

    private suspend fun updateContact(contact: ContactDTO) : ContactDTO? = withContext(Dispatchers.IO){
        // Set l'en-tête
        val url = URL("https://daa.iict.ch/contacts/${contact.id}")
        val connection = setUuid(url)
        connection.setRequestProperty("Content-Type", "application/json")
        connection.requestMethod = "PUT"

        // Ajoute le body
        val os = connection.outputStream
        os.write(Gson().toJson(contact).toByteArray())
        os.close()

        // Récupère la réponse
        val json = connection.url.readText(Charsets.UTF_8)

        if(!checkStatus(connection)) {
            return@withContext null
        }

        val type = object : TypeToken<ContactDTO>() {}.type
        Gson().fromJson<ContactDTO>(json, type)

    }

    private suspend fun deleteContact(id: String) : Boolean = withContext(Dispatchers.IO) {
        // Set l'en-tête
        val url = URL("https://daa.iict.ch/contacts/$id")
        val connection = setUuid(url)
        connection.requestMethod = "DELETE"

        checkStatus(connection)
    }

}