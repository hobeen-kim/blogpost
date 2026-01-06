package com.hobeen.batchpostcontent.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = ["com.hobeen.batchpostcontent.repository.props"], // ParsePropsRepository 위치 (패키지 분리 필요)
    entityManagerFactoryRef = "propsEntityManagerFactory",
    transactionManagerRef = "propsTransactionManager"
)
class PropsJpaConfig {

    @Bean(name = ["propsEntityManagerFactory"])
    fun propsEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
        @Qualifier("propsDataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        return builder
            .dataSource(dataSource)
            .packages("com.hobeen.batchpostcontent.entity.props") // ParseProps Entity 위치 (패키지 분리 필요)
            .persistenceUnit("props")
            .build()
    }

    @Bean(name = ["propsTransactionManager"])
    fun propsTransactionManager(
        @Qualifier("propsEntityManagerFactory") propsEntityManagerFactory: LocalContainerEntityManagerFactoryBean
    ): PlatformTransactionManager {
        return JpaTransactionManager(propsEntityManagerFactory.`object`!!)
    }
}
