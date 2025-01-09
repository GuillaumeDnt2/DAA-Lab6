package ch.heigvd.iict.and.rest.ui.screens.lab

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.heigvd.iict.and.rest.ContactsApplication
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModel
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModelFactory
// Add required imports
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ch.heigvd.iict.and.rest.R
import java.util.Locale
import java.text.SimpleDateFormat

// Add this new component for the edit/delete/cancel buttons
@Composable
fun EditActionButtons(
    onCancelClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onCancelClick,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("CANCEL")
        }

        Button(
            onClick = onDeleteClick,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("DELETE")
        }

        Button(
            onClick = onSaveClick,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("SAVE")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContactEdit(
    application: ContactsApplication,
    contactsViewModel: ContactsViewModel = viewModel(factory = ContactsViewModelFactory(application)),
    contactId: Long
) {
    val contact by contactsViewModel.getContactById(contactId).observeAsState()

    var context = LocalContext.current

    // State variables for form fields
    var name by remember { mutableStateOf("") }
    var firstname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var zip by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var selectedPhoneType by remember { mutableStateOf("FAX") }

    // Update the state variables whenever contact is updated
    LaunchedEffect(contact) {
        contact?.let {
            name = it.name ?: ""
            firstname = it.firstname ?: ""
            email = it.email ?: ""
            birthday = it.birthday?.time?.let { date ->
                SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
            } ?: ""
            address = it.address ?: ""
            zip = it.zip ?: ""
            city = it.city ?: ""
            phoneNumber = it.phoneNumber ?: ""
            selectedPhoneType = it.type?.name ?: "FAX"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = { contactsViewModel.setApplicationStatus(ContactsViewModel.ApplicationStatus.INITIAL) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(WindowInsets.ime.asPaddingValues())
        ) {
            Text(
                text = "Edit contact",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            if (contact == null) {
                // While the contact is loading, display a loading indicator
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            CustomOutlinedTextField(value = name, onValueChange = { name = it }, label = "Name")
            CustomOutlinedTextField(value = firstname, onValueChange = { firstname = it }, label = "Firstname")
            CustomOutlinedTextField(value = email, onValueChange = { email = it }, label = "E-Mail")
            CustomOutlinedTextField(value = birthday, onValueChange = { birthday = it }, label = "Birthday")
            CustomOutlinedTextField(value = address, onValueChange = { address = it }, label = "Address")
            CustomOutlinedTextField(value = zip, onValueChange = { zip = it }, label = "Zip")
            CustomOutlinedTextField(value = city, onValueChange = { city = it }, label = "City")

            PhoneTypeSelector(
                selectedPhoneType = selectedPhoneType,
                onPhoneTypeSelected = { selectedPhoneType = it }
            )

            CustomOutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = "Phone number"
            )

            EditActionButtons(
                onCancelClick = { contactsViewModel.setApplicationStatus(ContactsViewModel.ApplicationStatus.INITIAL) },
                onDeleteClick = {
                        contactsViewModel.delete(contact!!)
                        Toast.makeText(context, "Contact deleted successfully!", Toast.LENGTH_SHORT).show()
                        contactsViewModel.setApplicationStatus(ContactsViewModel.ApplicationStatus.INITIAL)
                    },
                onSaveClick = {
                        //TODO : Tell backend that the user has been updated
                    AppContactsUtilities().handleSaveOrUpdateClick(
                        context = context,
                        name = name,
                        firstname = firstname,
                        email = email,
                        birthday = birthday,
                        address = address,
                        zip = zip,
                        city = city,
                        phoneNumber = phoneNumber,
                        selectedPhoneType = selectedPhoneType,
                        contactsViewModel = contactsViewModel,
                        update = true)
                }
            )
        }
    }
}

private object PreviewData {
    const val name = "Pelletier"
    const val firstname = "Bernard"
    const val email = "b.pelletier@gmel.com"
    const val birthday = "26.12.2003"
    const val address = "Avenue des Sports 20"
    const val zip = "1400"
    const val city = "Yverdon-les-Bains"
    const val phoneNumber = "+41 24 123 10 01"
    const val phoneType = "Fax"
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AppContactEditPreview() {
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("REST Lab") },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Edit contact",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                CustomOutlinedTextField(value = PreviewData.name, onValueChange = { }, label = "Name")
                CustomOutlinedTextField(value = PreviewData.firstname, onValueChange = { }, label = "Firstname")
                CustomOutlinedTextField(value = PreviewData.email, onValueChange = { }, label = "E-Mail")
                CustomOutlinedTextField(value = PreviewData.birthday, onValueChange = { }, label = "Birthday")
                CustomOutlinedTextField(value = PreviewData.address, onValueChange = { }, label = "Address")
                CustomOutlinedTextField(value = PreviewData.zip, onValueChange = { }, label = "Zip")
                CustomOutlinedTextField(value = PreviewData.city, onValueChange = { }, label = "City")

                PhoneTypeSelector(
                    selectedPhoneType = PreviewData.phoneType,
                    onPhoneTypeSelected = { }
                )

                CustomOutlinedTextField(
                    value = PreviewData.phoneNumber,
                    onValueChange = { },
                    label = "Phone number"
                )

                EditActionButtons(
                    onCancelClick = { },
                    onDeleteClick = { },
                    onSaveClick = { }
                )
            }
        }
    }
}
