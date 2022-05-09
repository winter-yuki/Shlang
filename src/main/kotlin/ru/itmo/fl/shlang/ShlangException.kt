package ru.itmo.fl.shlang

abstract class ShlangException(message: String? = null, e: Throwable? = null) :
    RuntimeException(message, e)
