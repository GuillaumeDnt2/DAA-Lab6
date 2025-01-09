package ch.heigvd.iict.and.rest.models


/**
 * Contact de la DB distante
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
