# Form Validation Reference

## Shared validators — `common/ValidationUtils.kt`

| Function | Rule | Used in |
|---|---|---|
| `String.isValidEmail()` | contains `@`, domain contains `.`, length > 5 | ForgotPasswordScreen, SignUpScreen |
| `String.isValidPassword()` | length ≥ 8 | CreateNewPasswordScreen, SignUpScreen |

Import from `ke.co.smartroundclinic.doctor.common`.

## UI pattern

Errors are derived values (never stored in state) and are shown inline below the field:

```kotlin
val emailError = if (email.isNotBlank() && !email.isValidEmail()) "Enter a valid email address" else null

OutlinedTextField(
    value = email,
    onValueChange = { email = it },
    isError = emailError != null,
    ...
)
if (emailError != null) {
    Text(
        text = emailError,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(start = 4.dp, top = 2.dp),
    )
}
```

**Key rules:**
- Only show an error after the user has typed something (`field.isNotBlank() && !isValid()`). Never show an error on an empty field.
- Derive `canSubmit` from all error values rather than repeating the logic on the button.
- Never store the error message in a `StateFlow` or `mutableStateOf` — keep it a derived `val` inside the composable.

## Per-screen rules

### ForgotPasswordScreen
| Field | Rule | Error message |
|---|---|---|
| Email | valid email format | "Enter a valid email address" |

`canProceed = email.isNotBlank() && emailError == null && !isLoading`

### CreateNewPasswordScreen
| Field | Rule | Error message |
|---|---|---|
| New Password | ≥ 8 characters | "Password must be at least 8 characters" |
| Confirm Password | matches New Password | "Passwords do not match" |

`canSubmit = newPassword.isNotBlank() && passwordError == null && confirmError == null && confirmPassword == newPassword && !isLoading`

### SignUpScreen
Validation is handled locally in `SignUpScreen.kt` via private extensions (`isValidEmail`, `isValidDate`, `isOldEnough`). These overlap with `ValidationUtils.kt` — if new screens need date or age validation, extract those helpers to `ValidationUtils.kt` as well.

## Adding a new validator

1. Add a `fun String.isValidX(): Boolean` extension to `common/ValidationUtils.kt`.
2. Derive the error message inline in the composable.
3. Update this file.
