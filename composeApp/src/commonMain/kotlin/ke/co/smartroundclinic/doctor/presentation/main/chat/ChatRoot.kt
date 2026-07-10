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
import ke.co.smartroundclinic.doctor.domain.model.AppointmentStatus
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
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
) {
    val backStack = retain { mutableStateListOf<NavKey>(ChatList) }
    val isAtRoot = backStack.size == 1
    val vm: ConsultationViewModel = koinViewModel()
    val medicalRecordVm: MedicalRecordViewModel = koinViewModel()

    SideEffect { onAtRootChanged(isAtRoot) }

    LaunchedEffect(pendingConversation) {
        if (pendingConversation != null) {
            backStack.removeAll { it is Conversation }
            backStack.add(pendingConversation)
            onPendingNavigated()
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
                    onProfileClick = onProfileClick,
                    onNotificationsClick = onNotificationsClick,
                )
            }
            entry<Conversation> { dest ->
                LaunchedEffect(dest.latestAppointmentId) {
                    vm.startConsultation(dest.latestAppointmentId)
                }
                LaunchedEffect(dest.patientId) {
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
                    session = vm.activeSession,
                    messages = vm.messages,
                    isStartingSession = vm.isStartingSession,
                    isLoadingHistory = vm.isLoadingHistory,
                    isLoadingMoreHistory = vm.isLoadingMoreHistory,
                    hasMoreHistory = vm.hasMoreHistory,
                    onLoadMoreHistory = { vm.loadMoreHistory(vm.currentUserId, dest.patientId) },
                    isConnected = vm.isConnected,
                    isUploadingFile = vm.isUploadingFile,
                    isCallEnabled = appointment?.status == AppointmentStatus.CONFIRMED,
                    pendingFiles = vm.pendingFiles,
                    currentUserId = vm.currentUserId,
                    patientBio = medicalRecordVm.patientBio,
                    patientHistory = medicalRecordVm.patientHistory.toList(),
                    onBack = {
                        vm.closeSession()
                        backStack.removeLastOrNull()
                    },
                    onVoiceCall = { backStack.add(Call(vm.activeSession?.id ?: "", isVideo = false)) },
                    onVideoCall = { backStack.add(Call(vm.activeSession?.id ?: "", isVideo = true)) },
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
                    onJoin = { vm.joinCall(dest.sessionId) },
                    onEnd = {
                        vm.endCall()
                        backStack.removeLastOrNull()
                    },
                )
            }
        },
    )
}
