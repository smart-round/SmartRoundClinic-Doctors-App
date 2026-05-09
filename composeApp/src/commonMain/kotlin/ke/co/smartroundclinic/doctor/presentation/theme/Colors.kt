package ke.co.smartroundclinic.doctor.presentation.theme


import androidx.compose.ui.graphics.Color

// =============================================================================
// SmartRound Clinic — Color Tokens
// Light theme only. No dark scheme.
// Seed: Primary #E84E1C · Deep accent #59131C · Neutral #393938
// =============================================================================


// ── Primary tonal ramp (orange-red) ──────────────────────────────────────────

val Primary0   = Color(0xFF000000)
val Primary10  = Color(0xFF1A0500)
val Primary20  = Color(0xFF7A1F00)
val Primary30  = Color(0xFFB83200)
val Primary40  = Color(0xFFE84E1C)   // ★ brand orange — primary button fill
val Primary70  = Color(0xFFFF8A65)
val Primary80  = Color(0xFFFFB59B)
val Primary90  = Color(0xFFFFDBD0)   // tonal button / chip background
val Primary95  = Color(0xFFFFEDE8)
val Primary99  = Color(0xFFFFF8F6)
val Primary100 = Color(0xFFFFFFFF)


// ── Secondary tonal ramp (muted rose) ────────────────────────────────────────

val Secondary10  = Color(0xFF2C1512)
val Secondary20  = Color(0xFF4E2523)
val Secondary30  = Color(0xFF6E3E3B)
val Secondary40  = Color(0xFF8F5A57)
val Secondary80  = Color(0xFFD9B9B5)
val Secondary90  = Color(0xFFF5DDD9)   // secondaryContainer
val Secondary95  = Color(0xFFFAEEEB)
val Secondary99  = Color(0xFFFFF8F6)


// ── Tertiary tonal ramp (warm teal — complementary accent) ───────────────────

val Tertiary10  = Color(0xFF00201E)
val Tertiary20  = Color(0xFF004845)
val Tertiary30  = Color(0xFF006B67)
val Tertiary40  = Color(0xFF1B8F8A)   // confirmed / available chip
val Tertiary80  = Color(0xFF8DD8D3)
val Tertiary90  = Color(0xFFC8F0EC)   // tertiaryContainer
val Tertiary99  = Color(0xFFF4FBFA)


// ── Neutral ramp ─────────────────────────────────────────────────────────────

val Neutral0  = Color(0xFF000000)
val Neutral10 = Color(0xFF1C1B1B)   // onBackground
val Neutral20 = Color(0xFF393938)   // onSurfaceVariant / body text
val Neutral40 = Color(0xFF5C5A59)
val Neutral60 = Color(0xFF939190)   // placeholder / hint text
val Neutral80 = Color(0xFFCAC5C3)   // outline
val Neutral90 = Color(0xFFE8E3E1)   // surfaceVariant
val Neutral95 = Color(0xFFF5F0EE)   // surface
val Neutral99 = Color(0xFFFFFBFF)   // background
val Neutral100 = Color(0xFFFFFFFF)


// ── Error ramp ───────────────────────────────────────────────────────────────

val Error10  = Color(0xFF410002)
val Error40  = Color(0xFFB3261E)
val Error90  = Color(0xFFFCE8E6)


// ── Light color roles ─────────────────────────────────────────────────────────
// Named roles used throughout the app — always light, no dark variants.

val Primary            = Primary40
val OnPrimary          = Neutral100
val PrimaryContainer   = Primary90
val OnPrimaryContainer = Primary10

val Secondary            = Secondary40
val OnSecondary          = Neutral100
val SecondaryContainer   = Secondary90
val OnSecondaryContainer = Secondary10

val Tertiary            = Tertiary40
val OnTertiary          = Neutral100
val TertiaryContainer   = Tertiary90
val OnTertiaryContainer = Tertiary10

val Background   = Neutral99
val OnBackground = Neutral10
val Surface      = Neutral95
val OnSurface    = Neutral10
val SurfaceVariant    = Neutral90
val OnSurfaceVariant  = Neutral20
val Outline           = Neutral80
val OutlineVariant    = Neutral90

val Error            = Error40
val OnError          = Neutral100
val ErrorContainer   = Error90
val OnErrorContainer = Error10

// Gradient pair used on the header banner and splash screen
val GradientStart = Primary40          // #E84E1C
val GradientEnd   = Color(0xFF59131C)  // deep crimson from Figma
val ButtonGradientStart = Color(0XFFE84E1C)
val ButtonGradientEnd = Color(0XFF5F151C)
val TopAppBarGradientStart = Color(0XFFE84E1C)
val TopAppBarGradientEnd = Color(0XFF91291C)

// Appointment card left accent bar
val CardAccent = Primary40

// ── Custom semantic tokens ────────────────────────────────────────────────────

val StatusPublished = Color(0xFF0E9800)   // article published badge
val StatusSuspended = Color(0xFFEA1D25)   // article suspended badge
val StatusPending   = Primary40           // appointment pending
val StatusConfirmed = Tertiary40          // appointment confirmed

// Appointment card background (FEFAF8 from Figma)
val CardBackground  = Color(0xFFFEFAF8)

// Search bar overlay (rgba(0,0,0,0.21) on gradient)
val SearchBarOverlay = Color(0xFF1C1B1B).copy(alpha = 0.21f)