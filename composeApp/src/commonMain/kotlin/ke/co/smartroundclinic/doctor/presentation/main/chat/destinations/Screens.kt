package ke.co.smartroundclinic.doctor.presentation.main.chat.destinations

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object ChatList : NavKey

@Serializable
data class Conversation(val chatId: Int) : NavKey

@Serializable
data class Call(val chatId: Int, val isVideo: Boolean) : NavKey
