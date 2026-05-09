package ke.co.smartroundclinic.doctor.presentation.theme


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// =============================================================================
// SmartRound Clinic — Shape tokens
// Matches corner radii from the Figma spec (light theme only)
// =============================================================================

val SmartRoundShapes = Shapes(
    // extraSmall — input fields, small chips, status badges
    extraSmall = RoundedCornerShape(4.dp),

    // small — "View" button outline, filter pills
    small = RoundedCornerShape(8.dp),

    // medium — appointment cards, message rows (Rectangle 9 in Figma)
    medium = RoundedCornerShape(12.dp),

    // large — profile photo, CTA buttons, bottom sheet (Rectangle 46)
    large = RoundedCornerShape(14.dp),

    // extraLarge — splash screen, home header banner (40dp in Figma)
    extraLarge = RoundedCornerShape(40.dp),
)

// ── Named aliases matching Figma layer comments ───────────────────────────────

/** Appointment / message / article cards */
val ShapeCard   = RoundedCornerShape(12.dp)

/** Primary CTA — "Sign In", "Next", "Get Started" */
val ShapeButton = RoundedCornerShape(14.dp)

/** Text input fields */
val ShapeInput  = RoundedCornerShape(4.dp)

/** Tab selector pill, search bar */
val ShapePill   = RoundedCornerShape(50.dp)

/** Avatar / profile photo */
val ShapeAvatar = RoundedCornerShape(50.dp)

/** Status badges — Published / Suspended / Pending */
val ShapeBadge  = RoundedCornerShape(12.dp)