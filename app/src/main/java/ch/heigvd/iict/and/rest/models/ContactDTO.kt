package ch.heigvd.iict.and.rest.models

import java.text.SimpleDateFormat
import java.util.Calendar


/**
 * Contact de la DB distante
 *
 * Authors : Dunant Guillaume, Junod Arthur, Häffner Edwin
 */
data class ContactDTO (
    val id: Long?,
    val name: String,
    val firstname: String?,
    val birthday : String?,
    val email: String?,
    val address: String?,
    val zip: String?,
    val city: String?,
    val type: String?,
    val phoneNumber: String?,
)

fun ContactDTO.toContact(): Contact {
    val birthday = Calendar.getInstance();
    birthday.setTime(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(this.birthday))
    return Contact(
        null,
        this.name,
        this.firstname,
        birthday,
        this.email,
        this.address,
        this.zip,
        this.city,
        this.type?.let { PhoneType.valueOf(it) },
        this.phoneNumber,
        this.id,
        Status.OK
    )
}


