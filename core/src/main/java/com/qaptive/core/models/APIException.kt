package com.qaptive.core.models

class APIException(val error: Error?) : Exception("API Error") {
}