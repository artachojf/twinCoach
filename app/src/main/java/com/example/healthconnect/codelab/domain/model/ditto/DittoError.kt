package com.example.healthconnect.codelab.domain.model.ditto

import com.example.healthconnect.codelab.data.model.failure.ResponseFailure

data class DittoError(val failure: ResponseFailure)