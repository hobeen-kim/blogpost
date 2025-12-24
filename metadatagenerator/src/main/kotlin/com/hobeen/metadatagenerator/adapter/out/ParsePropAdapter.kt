package com.hobeen.metadatagenerator.adapter.out

import com.hobeen.metadatagenerator.adapter.out.persistence.ParsePropsRepository
import com.hobeen.metadatagenerator.adapter.out.redis.RedisRepository
import com.hobeen.metadatagenerator.application.port.out.GetParsePropPort
import com.hobeen.metadatagenerator.domain.ParseProps
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional
class ParsePropAdapter (
    private val parsePropsRepository: ParsePropsRepository,
    private val redisRepository: RedisRepository,
): GetParsePropPort {

    override fun getParseProp(source: String): ParseProps {
        val cached = redisRepository.get(source)

        if(cached == null) {
            val entity = parsePropsRepository.getParsePropsBySource(source) ?: throw IllegalArgumentException("Source $source not found")

            val parserProps = entity.toParserProps()
            redisRepository.save(parserProps)

            return parserProps
        }

        return cached
    }
}