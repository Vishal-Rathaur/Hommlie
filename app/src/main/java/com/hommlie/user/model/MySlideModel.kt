package com.hommlie.user.model

data class MySlideModel(
    val url: String,
    val type: MediaType,
    val thumbnail: String
)

enum class MediaType {
    IMAGE, VIDEO
}
