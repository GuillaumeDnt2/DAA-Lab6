package ch.heigvd.iict.and.rest.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

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

fun Contact.toContactDTO(): ContactDTO? {
    if (this.remoteId == null
        || this.name == null
        || this.firstname == null
        || this.birthday == null
        || this.email == null
        || this.address == null
        || this.zip == null
        || this.city == null
        || this.type == null
        || this.phoneNumber == null
    ) {
        return null
    } else {
        return ContactDTO(
            this.remoteId!!,
            this.name,
            this.firstname,
            this.birthday.toString(),
            this.email,
            this.address,
            this.zip,
            this.city,
            this.type.toString(),
            this.phoneNumber
        )

    }
}