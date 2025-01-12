package ch.heigvd.iict.and.rest.ui.screens.lab

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

/**
 * Add contact composable layout
 *
 * Authors : Dunant Guillaume, Junod Arthur, Häffner Edwin
 */

/**
 * CustomOutlinedTextField
 * A composable function that creates an outlined text field with a label.
 */
@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * PhoneTypeSelector
 * A composable function that creates a row of radio buttons to select the type of phone number.
 */
@Composable
fun PhoneTypeSelector(
    selectedPhoneType: String,
    onPhoneTypeSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val phoneTypes = listOf("Home", "Mobile", "Office", "Fax")
        phoneTypes.forEach { type ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selectedPhoneType.equals(type, ignoreCase = true),
                    onClick = { onPhoneTypeSelected(type) }
                )
                Text(text = type)
            }
        }
    }
}

/**
 * ActionButtons
 * A composable function that creates two buttons to cancel or save the contact.
 */
@Composable
fun ActionButtons(
    onCancelClick: () -> Unit,
    onSaveClick:  () -> Unit
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
            onClick = onSaveClick,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("SAVE")
        }
    }
}

/**
 * AppContactAdd
 * A composable function that creates a screen to add a new contact.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContactAdd(
    application: ContactsApplication,
    contactsViewModel: ContactsViewModel = viewModel(factory = ContactsViewModelFactory(application))
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var firstname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var birthday by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var zip by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var selectedPhoneType by remember { mutableStateOf("Fax") }

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
                text = "Add contact",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 16.dp)
            )

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

            ActionButtons(
                onCancelClick = { contactsViewModel.setApplicationStatus(ContactsViewModel.ApplicationStatus.INITIAL) },
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
                        update = false)
                })
        }
    }
}

/**
 * AppContactAddPreview
 * The preview of the AppContactAdd composable function.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun AppContactAddPreview() {
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
                    text = "Add contact",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                CustomOutlinedTextField(value = "", onValueChange = { }, label = "Name")
                CustomOutlinedTextField(value = "", onValueChange = { }, label = "Firstname")
                CustomOutlinedTextField(value = "", onValueChange = { }, label = "E-Mail")
                CustomOutlinedTextField(value = "", onValueChange = { }, label = "Birthday")
                CustomOutlinedTextField(value = "", onValueChange = { }, label = "Address")
                CustomOutlinedTextField(value = "", onValueChange = { }, label = "Zip")
                CustomOutlinedTextField(value = "", onValueChange = { }, label = "City")
                PhoneTypeSelector(selectedPhoneType = "Fax", onPhoneTypeSelected = { })
                CustomOutlinedTextField(value = "", onValueChange = { }, label = "Phone number")

                ActionButtons(
                    onCancelClick = { },
                    onSaveClick = { }
                )
            }
        }
    }
}
