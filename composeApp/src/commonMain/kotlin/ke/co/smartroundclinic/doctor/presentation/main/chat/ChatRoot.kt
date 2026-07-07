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
                    appointments = vm.appointments,
                    onAppointmentClick = { appointment ->
                        backStack.add(Conversation(appointment.id, appointment.patientName))
                    },
                    onProfileClick = onProfileClick,
                    onNotificationsClick = onNotificationsClick,
                )
            }
            entry<Conversation> { dest ->
                LaunchedEffect(dest.appointmentId) {
                    vm.startConsultation(dest.appointmentId)
                }
                val appointment = vm.appointments.firstOrNull { it.id == dest.appointmentId }
                LaunchedEffect(appointment?.patientId) {
                    appointment?.patientId?.let { pid ->
                        medicalRecordVm.loadPatientBio(pid)
                        medicalRecordVm.loadPatientHistory(pid)
                    }
                }
                ConversationScreen(
                    patientName = dest.patientName,
                    patientPicture = appointment?.patientProfilePicture,
                    session = vm.activeSession,
                    messages = vm.messages,
                    isStartingSession = vm.isStartingSession,
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
                    vm.appointments.firstOrNull { it.id == c.appointmentId }?.patientProfilePicture
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
