package com.example.database

interface DatabaseSource {
    fun dao(): MyDao
}