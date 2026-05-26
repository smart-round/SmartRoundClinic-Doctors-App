package ke.co.smartroundclinic.doctor

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect val notificationPlatform: String