package net.mrgaabriel.bomdiagenerator.config

data class BomDiaConfig(
    val twitter: TwitterConfig
) {
    data class TwitterConfig(
        val enabled: Boolean,
        val consumerKey: String,
        val consumerSecret: String,
        val accessToken: String,
        val accessSecret: String
    )
}