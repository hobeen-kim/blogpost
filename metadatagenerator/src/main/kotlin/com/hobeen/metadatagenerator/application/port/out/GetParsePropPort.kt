package com.hobeen.metadatagenerator.application.port.out

import com.hobeen.metadatagenerator.domain.ParseProps

interface GetParsePropPort {

    fun getParseProp(source: String): ParseProps

    fun getParsePropFromPrimaryDb(source: String): ParseProps
}