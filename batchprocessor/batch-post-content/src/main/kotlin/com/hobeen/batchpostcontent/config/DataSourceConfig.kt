package com.hobeen.batchpostcontent.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource

@Configuration
class DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.post")
    fun postDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    @ConfigurationProperties("spring.datasource.post.configuration")
    fun postDataSource(postDataSourceProperties: DataSourceProperties): DataSource {
        return postDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource::class.java).build()
    }

    @Bean
    @ConfigurationProperties("spring.datasource.props")
    fun propsDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.props.configuration")
    fun propsDataSource(propsDataSourceProperties: DataSourceProperties): DataSource {
        return propsDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource::class.java).build()
    }
}
