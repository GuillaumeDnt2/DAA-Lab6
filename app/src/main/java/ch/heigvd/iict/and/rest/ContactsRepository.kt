package ch.heigvd.iict.and.rest

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import ch.heigvd.iict.and.rest.database.ContactsDao
import ch.heigvd.iict.and.rest.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class ContactsRepository(private val contactsDao: ContactsDao) {

    val allContacts = contactsDao.getAllContactsLiveData()

    fun getContactById(id: Long) = contactsDao.getContactByIdLiveData(id)

    val uuid = MutableLiveData<String>()

    suspend fun new(contact: Contact) = withContext(Dispatchers.IO){
        Log.d("Repo", "Creating new contact : ${contact.id} with name : ${contact.name}")
        val localId = contactsDao.insert(contact)
        val res = createContact(contact.toContactDTO())
        if (res != null){
            contact.id = localId
            contact.remoteId = res.id
            okStatus(contact)
        }else{
            Log.e("Repo", "Couldn't connect to remote server to create our contact")
        }
    }

    suspend fun update(contact: Contact) = withContext(Dispatchers.IO){
        Log.d("Repo", "Updating contact : ${contact.id} with remote id : ${contact.remoteId} and name : ${contact.name}")
        contactsDao.update(contact)
        val res = updateContact(contact.toContactDTO())
        if (res != null){
            okStatus(contact)
        }else{
            Log.e("Repo", "Couldn't connect to remote server to update our contact")
        }
    }

    suspend fun delete(contact: Contact) = withContext(Dispatchers.IO){
        Log.d("Repo", "Deleting contact : ${contact.id} with remote id : ${contact.remoteId} and name : ${contact.name}")
        contact.status = Status.DEL
        contactsDao.update(contact)
        val res = deleteContact(contact.remoteId.toString())
        if (res) {
            deleteConfirmed(contact)
        }else{
            Log.e("Repo", "Couldn't connect to remote server to delete our contact")
        }
    }

    private suspend fun deleteConfirmed(contact : Contact) = withContext(Dispatchers.IO){
        Log.d("Repo", "Remote deleting succeeded, totally deleting our local contact : ${contact.id}")
        contactsDao.delete(contact)
    }

    suspend fun refresh() = withContext(Dispatchers.IO){
        Log.d("Repo", "Starting refresh")
        if(allContacts.isInitialized){
            for(contact in allContacts.value!!){
                Log.d("Repo", "Defining what to do with contact : ${contact.id} with name : ${contact.name}, remote id : ${contact.remoteId} and status : ${contact.status}")
                when (contact.status) {
                    Status.OK -> {
                        Log.d("Repo", "Status of contact : ${contact.id} with name : ${contact.name} is OK nothing to Do")
                        continue
                    }
                    Status.DEL -> {
                        val res = deleteContact(contact.remoteId.toString())
                        if (res) {
                            deleteConfirmed(contact)
                        }else{
                            Log.e("Repo", "Couldn't connect to remote server to delete our contact")
                        }
                    }
                    Status.MOD -> {
                        val res = updateContact(contact.toContactDTO())
                        if (res != null){
                            okStatus(contact)
                        }else{
                            Log.e("Repo", "Couldn't connect to remote server to update our contact")
                        }
                    }
                    Status.NEW -> {
                        val res = createContact(contact.toContactDTO())
                        if (res != null){
                            contact.remoteId = res.id
                            okStatus(contact)
                        }else{
                            Log.e("Repo", "Couldn't connect to remote server to create our contact")
                        }
                    }
                    else -> {
                        error("Unexpected status from contact with id ${contact.id}")
                    }
                }
            }

            updateLocalDatabase()

            Log.d("Repo", "End refresh")
        }
    }

    suspend fun enroll(): Boolean = withContext(Dispatchers.IO){
        Log.d("Repo", "Enrollement started")

        try {
            //Récupère le nouveau uuid
            val url = URL("https://daa.iict.ch/enroll")
            val newUuid = url.readText()
            Log.d("Repo", "Enrollement was successful and gave us the uuid : $newUuid")
            uuid.postValue(newUuid)
            true
        } catch (e: Exception) {
            Log.e("Repo", "Enrollement failed")
            false
        }

    }

    suspend fun updateLocalDatabase() = withContext(Dispatchers.IO) {
        Log.d("Repo", "Updating local database")
        // Récupère les contacts depuis le serveur
        val contacts = fetchAll()

        if (contacts != null) {
            // Supprime la base de données local
            contactsDao.clearAllContacts()

            // Met à jour la base de données local
            for (contact in contacts) {
                Log.d("Repo", "Adding contact : ${contact.id} with name : ${contact.name}")
                contactsDao.insert(contact.toContact());
            }
        }
    }

    private fun okStatus(contact : Contact){
        Log.d("Repo", "Remote update succeeded putting status to OK")
        contact.status = Status.OK
        contactsDao.update(contact)
    }

    private fun setUuid(url: URL) : HttpURLConnection?{
        // Vérifie que l'uuid ne soit pas null
        uuid.value ?: return null

        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("X-UUID", uuid.value)
        return connection
    }

    private fun checkStatus(connection: HttpURLConnection) : Boolean {
        return connection.responseCode in 200..299
    }


    suspend fun fetchAll() : List<ContactDTO>? = withContext(Dispatchers.IO){
        Log.d("Repo", "Fetching all contacts")
        val url = URL("https://daa.iict.ch/contacts")
        val connection = setUuid(url)
        connection ?: return@withContext null

        try {
            val json = connection.inputStream.bufferedReader().use { it.readText() }

            if (!checkStatus(connection)) {
                Log.d("Repo", "Fetching all fail with status : ${connection.responseCode}")
                return@withContext null
            }

            val type = object : TypeToken<List<ContactDTO>>() {}.type
            Gson().fromJson<List<ContactDTO>>(json, type)

        } catch (e: Exception) {
            Log.e("Repo", "Fetching all fail")
            null
        }
    }

    suspend fun fetchContact(id: String) : ContactDTO? = withContext(Dispatchers.IO){
        val url = URL("https://daa.iict.ch/contacts/$id")
        val connection = setUuid(url)
        connection ?: return@withContext null

        try {
            val json = connection.inputStream.bufferedReader().use { it.readText() }

            if (!checkStatus(connection)) {
                return@withContext null
            }

            val type = object : TypeToken<ContactDTO>() {}.type
            Gson().fromJson<ContactDTO>(json, type)
        } catch (e: Exception) {
            Log.e("Repo", "Fetching contact fail")
            null
        }
    }

    private suspend fun createContact(contact: ContactDTO) : ContactDTO? = withContext(Dispatchers.IO){
        Log.d("Repo", "Creating contact")

        // Set l'en-tête
        val url = URL("https://daa.iict.ch/contacts")
        val connection = setUuid(url)
        connection ?: return@withContext null
        connection.setRequestProperty("Content-Type", "application/json")
        connection.requestMethod = "POST"

        try{
            // Ajoute le body
            val os = connection.outputStream
            Log.d("Repo", Gson().toJson(contact))
            os.write(Gson().toJson(contact).toByteArray())
            os.close()

            // Récupère la réponse
            val json = connection.inputStream.bufferedReader().use { it.readText() }

            // Vérifie que la réponse est OK
            if (!checkStatus(connection)) {
                return@withContext null
            }

            // Converti le JSON en objet et le retourne
            val type = object : TypeToken<ContactDTO>() {}.type
            Gson().fromJson<ContactDTO>(json, type)
        } catch (e: Exception) {
            Log.e("Repo", "Creating contact fail")
            null
        }

    }

    private suspend fun updateContact(contact: ContactDTO) : ContactDTO? = withContext(Dispatchers.IO){
        // Set l'en-tête
        val url = URL("https://daa.iict.ch/contacts/${contact.id}")
        val connection = setUuid(url)
        connection ?: return@withContext null
        connection.setRequestProperty("Content-Type", "application/json")
        connection.requestMethod = "PUT"
        Log.d("Repo", contact.toString())

        try {
            // Ajoute le body
            val os = connection.outputStream
            os.write(Gson().toJson(contact).toByteArray())
            os.close()

            // Récupère la réponse
            val json = connection.inputStream.bufferedReader().use { it.readText() }

            if (!checkStatus(connection)) {
                return@withContext null
            }

            val type = object : TypeToken<ContactDTO>() {}.type
            Gson().fromJson<ContactDTO>(json, type)
        } catch (e: Exception) {
            Log.e("Repo", "Updating contact fail")
            null
        }

    }

    private suspend fun deleteContact(id: String) : Boolean = withContext(Dispatchers.IO) {
        // Set l'en-tête
        val url = URL("https://daa.iict.ch/contacts/$id")
        val connection = setUuid(url)
        connection ?: return@withContext false
        connection.requestMethod = "DELETE"

        try {
            checkStatus(connection)
        } catch (e: Exception){
            Log.e("Repo", "Deleting contact fail")
            false
        }

    }

}