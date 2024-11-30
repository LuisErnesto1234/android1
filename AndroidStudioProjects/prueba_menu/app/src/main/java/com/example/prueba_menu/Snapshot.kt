package com.example.prueba_menu

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Snapshot (
    var id: String? = "",
    var title: String? = "",
    var photoUrl: String? = "",
    var LikeList: Map<String, Boolean> = mutableMapOf()
)