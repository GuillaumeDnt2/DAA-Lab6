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
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModel
import ch.heigvd.iict.and.rest.viewmodels.ContactsViewModelFactory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ch.heigvd.iict.and.rest.R
import java.util.Locale
import java.text.SimpleDateFormat
/**
 * Edit contact composable layout
 *
 * Authors : Dunant Guillaume, Junod Arthur, Häffner Edwin
 */

/**
 * EditActionButtons
 *
 * A composable that displays the action buttons for editing a contact. (Cancel, Delete, Save)
 */
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

/**
 * AppContactEdit
 *
 * A composable that displays the screen for editing or deleting a contact.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContactEdit(
    application: ContactsApplication,
    contactsViewModel: ContactsViewModel = viewModel(factory = ContactsViewModelFactory(application)),
    contactId: Long
) {
    val contact by contactsViewModel.getContactById(contactId).observeAsState()

    val context = LocalContext.current

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
                        update = true,
                        remoteId = contact?.remoteId,
                        id = contact?.id)
                }
            )
        }
    }
}

// Preview data for the AppContactEditPreview composable
private object PreviewData {
    const val NAME = "Pelletier"
    const val FIRSTNAME = "Bernard"
    const val EMAIL = "b.pelletier@gmel.com"
    const val BIRTHDAY = "26.12.2003"
    const val ADDRESS = "Avenue des Sports 20"
    const val ZIP = "1400"
    const val CITY = "Yverdon-les-Bains"
    const val PHONENUMBER = "+41 24 123 10 01"
    const val PHONETYPE = "Fax"
}

/**
 * AppContactEditPreview
 *
 * A preview of the AppContactEdit composable.
 */
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
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Edit contact",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                CustomOutlinedTextField(value = PreviewData.NAME, onValueChange = { }, label = "Name")
                CustomOutlinedTextField(value = PreviewData.FIRSTNAME, onValueChange = { }, label = "Firstname")
                CustomOutlinedTextField(value = PreviewData.EMAIL, onValueChange = { }, label = "E-Mail")
                CustomOutlinedTextField(value = PreviewData.BIRTHDAY, onValueChange = { }, label = "Birthday")
                CustomOutlinedTextField(value = PreviewData.ADDRESS, onValueChange = { }, label = "Address")
                CustomOutlinedTextField(value = PreviewData.ZIP, onValueChange = { }, label = "Zip")
                CustomOutlinedTextField(value = PreviewData.CITY, onValueChange = { }, label = "City")

                PhoneTypeSelector(
                    selectedPhoneType = PreviewData.PHONETYPE,
                    onPhoneTypeSelected = { }
                )

                CustomOutlinedTextField(
                    value = PreviewData.PHONENUMBER,
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
