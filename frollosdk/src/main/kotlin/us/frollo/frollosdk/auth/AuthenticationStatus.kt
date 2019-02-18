package us.frollo.frollosdk.auth

/**
 * Status of the FrolloSDK authentication with Frollo servers
 */
enum class AuthenticationStatus {
    /** User Authenticated */
    AUTHENTICATED,
    /** User logged out */
    LOGGED_OUT
}