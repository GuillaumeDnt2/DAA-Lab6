package ch.heigvd.iict.and.rest.viewmodels

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.heigvd.iict.and.rest.ContactsApplication
import ch.heigvd.iict.and.rest.models.Contact
import kotlinx.coroutines.launch

class ContactsViewModel(application: ContactsApplication) : AndroidViewModel(application) {

    enum class ApplicationStatus {
        EDIT,
        ADD,
        INITIAL
    }

    //TODO: Il faut également un UUID ici qui sera soit donné par l'Activité depuis les préférences
    // soit donné par l'inscription depuis le repository.
    // En gros strucutre: Préférences <-> Activité <-> ViewModel <-> Repository <-> Serveur
    // L'UUID va se balader entre tous ces participants.

    private val repository = application.repository

    val allContacts = repository.allContacts

    var uuid = repository.uuid

    private val _idToEdit = MutableLiveData<Long?>(null)
    val idToEdit: LiveData<Long?> = _idToEdit

    private val _applicationStatus = MutableLiveData<ApplicationStatus>(ApplicationStatus.INITIAL)
    val applicationStatus: LiveData<ApplicationStatus> = _applicationStatus

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
            repository.update(contact)
        }
    }

    fun enroll() {
        viewModelScope.launch{
            repository.enroll()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            repository.
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