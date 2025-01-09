package ch.heigvd.iict.and.rest.models

enum class PhoneType {
    HOME, OFFICE, MOBILE, FAX;

    companion object {
        /**
         * Returns the corresponding PhoneType for a given string representation.
         *
         * @param type The string representation of the phone type (case-insensitive).
         * @return The corresponding PhoneType, or null if no match is found.
         */
        fun fromString(type: String): PhoneType? {
            return when (type.uppercase()) {
                "HOME" -> HOME
                "OFFICE" -> OFFICE
                "MOBILE" -> MOBILE
                "FAX" -> FAX
                else -> null
            }
        }
    }
}