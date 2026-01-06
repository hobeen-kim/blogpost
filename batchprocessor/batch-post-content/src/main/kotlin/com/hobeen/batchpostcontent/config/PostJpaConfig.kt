package com.hobeen.batchpostcontent.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = ["com.hobeen.batchpostcontent.repository.post"], // PostRepository 위치 (패키지 분리 필요)
    entityManagerFactoryRef = "postEntityManagerFactory",
    transactionManagerRef = "postTransactionManager"
)
class PostJpaConfig {

    @Primary
    @Bean(name = ["postEntityManagerFactory"])
    fun postEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
        @Qualifier("postDataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        return builder
            .dataSource(dataSource)
            .packages("com.hobeen.batchpostcontent.entity.post") // Post Entity 위치 (패키지 분리 필요)
            .persistenceUnit("post")
            .build()
    }

    @Primary
    @Bean(name = ["postTransactionManager"])
    fun postTransactionManager(
        @Qualifier("postEntityManagerFactory") postEntityManagerFactory: LocalContainerEntityManagerFactoryBean
    ): PlatformTransactionManager {
        return JpaTransactionManager(postEntityManagerFactory.`object`!!)
    }
}
