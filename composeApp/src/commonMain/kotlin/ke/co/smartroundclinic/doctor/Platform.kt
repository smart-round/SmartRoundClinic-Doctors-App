package ke.co.smartroundclinic.doctor

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform