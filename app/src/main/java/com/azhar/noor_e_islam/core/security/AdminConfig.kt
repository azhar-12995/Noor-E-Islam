package com.azhar.noor_e_islam.core.security

/**
 * Centralised admin identity check.
 *
 * The Noor-e-Islam app uses a single hard-coded admin email; whoever signs in
 * with this address is routed to the admin dashboard instead of the home
 * screen. Firestore security rules should ALSO enforce admin-only writes via
 * a custom claim — this constant is just the UI gate.
 */
object AdminConfig {
    const val ADMIN_EMAIL = "nooreislamadmin2026@gmail.com"

    fun isAdminEmail(email: String?): Boolean =
        !email.isNullOrBlank() && email.trim().equals(ADMIN_EMAIL, ignoreCase = true)
}

