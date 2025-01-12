package ch.heigvd.iict.and.rest.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

/**
 * Entity class for a contact
 *
 * Authors : Dunant Guillaume, Junod Arthur, Häffner Edwin
 */
@Entity
data class Contact(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var name: String,
    var firstname: String?,
    var birthday: Calendar?,
    var email: String?,
    var address: String?,
    var zip: String?,
    var city: String?,
    var type: PhoneType?,
    var phoneNumber: String?,
    var remoteId: Long?,
    var status: Status?
)

fun Contact.toContactDTO(): ContactDTO {
        return ContactDTO(
            this.remoteId,
            this.name,
            this.firstname,
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(this.birthday?.time),
            this.email,
            this.address,
            this.zip,
            this.city,
            this.type.toString(),
            this.phoneNumber
        )
}