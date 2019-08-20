package com.example.mytodolist.utils

class FieldMissingException(message: String) : Exception(message) {
    companion object {
        const val FIELD_EMAIL = "FIELD_EMAIL"
        const val FIELD_PASSWORD = "FIELD_PASSWORD"
        const val FIELD_PASSWORD_CONFIRM = "FIELD_PASSWORD_CONFIRM"
        const val FIELD_TITLE = "FIELD_TITLE"
    }
}
