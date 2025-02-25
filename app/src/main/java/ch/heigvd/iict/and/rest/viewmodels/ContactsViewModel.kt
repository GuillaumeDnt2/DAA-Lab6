package ch.heigvd.iict.and.rest.viewmodels

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import ch.heigvd.iict.and.rest.ContactsApplication
import ch.heigvd.iict.and.rest.models.Contact
import kotlinx.coroutines.launch

/**
 * ViewModel for the Contacts.
 *
 * Authors : Dunant Guillaume, Junod Arthur, Häffner Edwin
 */
class ContactsViewModel(application: ContactsApplication) : AndroidViewModel(application) {

    enum class ApplicationStatus {
        EDIT,
        ADD,
        INITIAL
    }

    private val repository = application.repository

    val allContacts = repository.allContacts

    private val _idToEdit = MutableLiveData<Long?>(null)
    val idToEdit: LiveData<Long?> = _idToEdit

    private val _applicationStatus = MutableLiveData<ApplicationStatus>(ApplicationStatus.INITIAL)
    val applicationStatus: LiveData<ApplicationStatus> = _applicationStatus

    fun getUuid() = repository.uuid

    fun setIdToEdit(id: Long?) {
        _idToEdit.value = id
    }

    fun setApplicationStatus(status: ApplicationStatus) {
        _applicationStatus.value = status
    }

    fun new(contact : Contact){
        viewModelScope.launch{
            repository.new(contact)
        }
    }

    fun delete(contact : Contact){
        viewModelScope.launch{
            repository.delete(contact)
        }
    }

    fun update(contact : Contact){
        viewModelScope.launch{
            Log.d("VM", "Updating contact : ${contact.id} with name : ${contact.name}")
            repository.update(contact)
        }
    }

    fun enroll() {
        viewModelScope.launch{
            repository.enroll()
        }
    }

    fun updateLocalDatabase() {
        viewModelScope.launch {
            repository.updateLocalDatabase()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refresh()
        }
    }

    fun getContactById(id: Long) = repository.getContactById(id)

}

class ContactsViewModelFactory(private val application: ContactsApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}