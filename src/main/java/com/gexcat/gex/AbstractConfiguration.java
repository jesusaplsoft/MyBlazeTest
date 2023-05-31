package com.gexcat.gex;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author Vlad Mihalcea
 * @author Jesús Marín
 */
@ComponentScan(basePackages = { "com.gexcat.gex", })
public abstract class AbstractConfiguration {

    @Value("${jdbc.maximumPoolSize}")
    private int maximumPoolSize;

    @Value("${hibernate.dialect}")
    private String hibernateDialect;

    @Value("${hibernate.connection.autocommit}")
    private Boolean connectionAutocommit;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertiesConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean(destroyMethod = "close")
    public HikariDataSource dataSource()
        throws SQLException {
        final var hikariConfig = new HikariConfig();
        hikariConfig.setMaximumPoolSize(this.maximumPoolSize);
        hikariConfig.setDataSource(actualDataSource());
        hikariConfig.setAutoCommit(this.connectionAutocommit);
        hikariConfig.setMinimumIdle(0);
        hikariConfig.setIdleTimeout(TimeUnit.SECONDS.toMillis(60));
        hikariConfig.setInitializationFailTimeout(-1);
        hikariConfig.setConnectionTimeout(Integer.MAX_VALUE);
        return new HikariDataSource(hikariConfig);
    }

    protected abstract DataSource actualDataSource()
        throws SQLException;

    protected abstract LocalContainerEntityManagerFactoryBean entityManagerFactory()
        throws SQLException;

    protected abstract PlatformTransactionManager transactionManager()
        throws SQLException;

    @Bean
    public TransactionTemplate transactionTemplate(final EntityManagerFactory entityManagerFactory)
        throws SQLException {
        return new TransactionTemplate(transactionManager());
    }

    protected abstract JpaVendorAdapter jpaVendorAdapter();

    protected abstract String[] packagesToScan();
}
