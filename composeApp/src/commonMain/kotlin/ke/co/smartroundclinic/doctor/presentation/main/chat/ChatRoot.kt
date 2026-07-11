package ke.co.smartroundclinic.doctor.presentation.main.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ke.co.smartroundclinic.doctor.presentation.main.bookings.MedicalRecordViewModel
import ke.co.smartroundclinic.doctor.presentation.main.chat.destinations.Call
import ke.co.smartroundclinic.doctor.presentation.main.chat.destinations.ChatList
import ke.co.smartroundclinic.doctor.presentation.main.chat.destinations.Conversation
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.CallScreen
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.ChatListScreen
import ke.co.smartroundclinic.doctor.presentation.main.chat.ui.ConversationScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatRoot(
    modifier: Modifier = Modifier,
    onAtRootChanged: (Boolean) -> Unit = {},
    pendingConversation: Conversation? = null,
    onPendingNavigated: () -> Unit = {},
    pendingCall: Call? = null,
    onPendingCallNavigated: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
) {
    val backStack = retain { mutableStateListOf<NavKey>(ChatList) }
    val isAtRoot = backStack.size == 1
    val vm: ConsultationViewModel = koinViewModel()
    val medicalRecordVm: MedicalRecordViewModel = koinViewModel()

    SideEffect { onAtRootChanged(isAtRoot) }

    // The chat list has no socket of its own — keep its online/last-seen previews live by polling
    // only while it's the visible screen (stop once a specific conversation is opened).
    LaunchedEffect(isAtRoot) {
        if (isAtRoot) vm.startThreadsPolling() else vm.stopThreadsPolling()
    }

    LaunchedEffect(pendingConversation) {
        if (pendingConversation != null) {
            backStack.removeAll { it is Conversation }
            backStack.add(pendingConversation)
            onPendingNavigated()
        }
    }

    // Fires after the pendingConversation effect above (both are set together from the same
    // notification event), so Conversation is already on the stack for Call's entry to read
    // the patient name/picture off of.
    LaunchedEffect(pendingCall) {
        if (pendingCall != null) {
            backStack.removeAll { it is Call }
            backStack.add(pendingCall)
            onPendingCallNavigated()
        }
    }

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<ChatList> {
                ChatListScreen(
                    threads = vm.threads,
                    onThreadClick = { thread ->
                        backStack.add(Conversation(thread.patientId, thread.counterpartName, thread.latestAppointmentId))
                    },
                    onDeleteThread = { thread -> vm.deleteThread(thread.doctorId, thread.patientId) },
                    onProfileClick = onProfileClick,
                    onNotificationsClick = onNotificationsClick,
                )
            }
            entry<Conversation> { dest ->
                LaunchedEffect(dest.patientId) {
                    vm.connectToThread(dest.patientId)
                    vm.loadNextAppointment(dest.patientId)
                    medicalRecordVm.loadPatientBio(dest.patientId)
                    medicalRecordVm.loadPatientHistory(dest.patientId)
                }
                // currentUserId loads asynchronously from Room — re-key on it so that if this fires
                // before it's populated (cold start / fresh login), it retries once the id is ready
                // instead of calling loadMergedHistory with an empty doctorId forever.
                LaunchedEffect(dest.patientId, vm.currentUserId) {
                    if (vm.currentUserId.isNotBlank()) {
                        vm.loadMergedHistory(vm.currentUserId, dest.patientId)
                    }
                }
                val appointment = vm.appointments.firstOrNull { it.id == dest.latestAppointmentId }
                val patientPicture = vm.threads.firstOrNull { it.patientId == dest.patientId }?.counterpartPicture
                    ?: appointment?.patientProfilePicture
                ConversationScreen(
                    patientName = dest.patientName,
                    patientPicture = patientPicture,
                    appointment = vm.nextAppointment,
                    onLockedCallClick = { vm.notifyCallLocked(it) },
                    messages = vm.messages,
                    isLoadingHistory = vm.isLoadingHistory,
                    isLoadingMoreHistory = vm.isLoadingMoreHistory,
                    hasMoreHistory = vm.hasMoreHistory,
                    onLoadMoreHistory = { vm.loadMoreHistory(vm.currentUserId, dest.patientId) },
                    isConnected = vm.isConnected,
                    isUploadingFile = vm.isUploadingFile,
                    pendingFiles = vm.pendingFiles,
                    currentUserId = vm.currentUserId,
                    patientBio = medicalRecordVm.patientBio,
                    patientHistory = medicalRecordVm.patientHistory.toList(),
                    otherPartyTyping = vm.otherPartyTyping,
                    otherPartyOnline = vm.otherPartyOnline,
                    otherPartyLastSeenAt = vm.otherPartyLastSeenAt,
                    onTyping = vm::sendTypingEvent,
                    onBack = {
                        vm.disconnect()
                        backStack.removeLastOrNull()
                    },
                    onVoiceCall = { backStack.add(Call(dest.patientId, isVideo = false)) },
                    onVideoCall = { backStack.add(Call(dest.patientId, isVideo = true)) },
                    onSendText = vm::sendText,
                    onSendFile = vm::sendFile,
                )
            }
            entry<Call> { dest ->
                val callConversation = backStack.filterIsInstance<Conversation>().firstOrNull()
                val patientName = callConversation?.patientName ?: "Patient"
                val patientPicture = callConversation?.let { c ->
                    vm.threads.firstOrNull { it.patientId == c.patientId }?.counterpartPicture
                        ?: vm.appointments.firstOrNull { it.id == c.latestAppointmentId }?.patientProfilePicture
                }
                CallScreen(
                    participantName = patientName,
                    participantPicture = patientPicture,
                    selfPicture = vm.currentUserProfilePicture,
                    isVideo = dest.isVideo,
                    joinState = vm.callJoinState,
                    onJoin = { vm.joinCall(dest.otherUserId) },
                    onEnd = {
                        vm.endCall()
                        backStack.removeLastOrNull()
                    },
                )
            }
        },
    )
}
