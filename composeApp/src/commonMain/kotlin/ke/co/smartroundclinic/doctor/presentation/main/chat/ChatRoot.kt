package ke.co.smartroundclinic.doctor.presentation.main.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ke.co.smartroundclinic.doctor.presentation.main.chat.destinations.Call
import ke.co.smartroundclinic.doctor.presentation.main.chat.destinations.ChatList
import ke.co.smartroundclinic.doctor.presentation.main.chat.destinations.Conversation
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.CallScreen
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.ChatListScreen
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.ConversationScreen

internal data class MessageUi(
    val id: Int,
    val text: String,
    val fromMe: Boolean,
    val time: String,
)

internal data class ChatUi(
    val id: Int,
    val participantName: String,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int = 0,
    val messages: List<MessageUi> = emptyList(),
)

private val jumaMessages = listOf(
    MessageUi(1, "Hello doctor, I have not been feeling well lately.", false, "13:10"),
    MessageUi(2, "I'm sorry to hear that. What symptoms are you experiencing?", true, "13:11"),
    MessageUi(3, "I have a persistent cough and a slight fever.", false, "13:12"),
    MessageUi(4, "How long have you had these symptoms?", true, "13:13"),
    MessageUi(5, "About 3 days now. It's getting worse.", false, "13:14"),
    MessageUi(6, "I'd recommend coming in for a check-up. Can you make it Wednesday at 8:00 AM?", true, "13:15"),
    MessageUi(7, "Yes, Wednesday works perfectly for me.", false, "13:20"),
    MessageUi(8, "Okay noted, we will see you on Wednesday. Please rest and stay hydrated.", true, "13:25"),
)

private val sampleChats = listOf(
    ChatUi(1, "Juma Kongis", "Okay noted, we will see you on Wednesday.", "13:25", unreadCount = 2, messages = jumaMessages),
    ChatUi(2, "Alice Wachira", "You: Thanks doctor", "27 Oct"),
    ChatUi(3, "Simon Mulatu", "Please get me another appointment", "25 Oct"),
    ChatUi(4, "Memory Wanjiku", "Okay sure", "23 Oct"),
    ChatUi(5, "Alliance Auma", "How are you feeling today?", "20 Oct"),
    ChatUi(6, "Newton Gil", "I will be there shortly", "18 Oct"),
)

@Composable
fun ChatRoot(
    modifier: Modifier = Modifier,
    onAtRootChanged: (Boolean) -> Unit = {},
) {
    val backStack = retain { mutableStateListOf<NavKey>(ChatList) }
    val isAtRoot = backStack.size == 1

    SideEffect { onAtRootChanged(isAtRoot) }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<ChatList> {
                ChatListScreen(
                    chats = sampleChats,
                    onChatClick = { chat -> backStack.add(Conversation(chat.id)) },
                )
            }
            entry<Conversation> { dest ->
                val chat = sampleChats.first { it.id == dest.chatId }
                ConversationScreen(
                    chat = chat,
                    onBack = { backStack.removeLastOrNull() },
                    onVoiceCall = { backStack.add(Call(chatId = dest.chatId, isVideo = false)) },
                    onVideoCall = { backStack.add(Call(chatId = dest.chatId, isVideo = true)) },
                )
            }
            entry<Call> { dest ->
                val chat = sampleChats.first { it.id == dest.chatId }
                CallScreen(
                    chat = chat,
                    isVideo = dest.isVideo,
                    onEnd = { backStack.removeLastOrNull() },
                )
            }
        },
    )
}
