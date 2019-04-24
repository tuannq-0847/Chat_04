package com.sun.chat_04.data.model

data class UserDistanceWrapper(val user: User, val distance: Float) : Comparable<UserDistanceWrapper> {
    override fun compareTo(other: UserDistanceWrapper): Int = when {
        other.distance == distance -> 0
        other.distance < distance -> 1
        else -> -1
    }
}
