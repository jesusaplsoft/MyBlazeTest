package com.gexcat.gex.cfg;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.engine.jdbc.internal.Formatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.integration.view.spring.EnableEntityViews;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViews;
import com.blazebit.persistence.view.spi.EntityViewConfiguration;
import com.gexcat.gex.AuditorAwareImpl;

import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.listener.logging.SystemOutQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;

/**
 * @author Vlad Mihalcea
 * @author Jesús Marín
 */
//@ComponentScan(basePackages = { "com.gexcat.gex.repository" })
@PropertySource({ "classpath:db_jpa.properties", "classpath:db_jpa-test.properties" })
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableTransactionManagement
//@EnableBlazeRepositories(basePackages = "com.gexcat.gex.repository")
@EnableEntityViews("com.gexcat.gex.repository.view")
@EnableJpaRepositories(bootstrapMode = BootstrapMode.LAZY, basePackages = "com.gexcat.gex.repository")
public class GexCatTestConfiguration
    extends AbstractJpaConfiguration {

    @Value("${connection.url}")
    private String jdbcUrl;

    @Value("${connection.driverClassName}")
    private String jdbcDriverClassName;

    @Value("${connection.username}")
    private String jdbcUser;

    @Value("${connection.password}")
    private String jdbcPassword;

    @Value("${hibernate.cache.use_query_cache}")
    private String cacheUseQueryCache;

    @Value("${hibernate.cache.use_second_level_cache}")
    private String cacheUseSecondLevelCache;

    @Value("${hibernate.cglib.use_reflection_optimizer}")
    private String cglibUseReflectionOptimizer;

    @Value("${hibernate.connection.charSet}")
    private String charSet;

    @Value("${hibernate.connection.provider_class}")
    private String providerClass;

    @Value("${hibernate.current_session_context_class}")
    private String currentSessionContextClass;

    @Value("${hibernate.dialect}")
    private String dialect;

    @Value("${hibernate.format_sql}")
    private String formatSql;

    @Value("${hibernate.hbm2ddl.auto}")
    private String hbm2ddl;

    @Value("${hibernate.jdbc.batch_size}")
    private String jdbcBatchSize;

    @Value("${hibernate.jdbc.fetch_size}")
    private String jdbcFetchSize;

    @Value("${hibernate.max_fetch_depth}")
    private String maxFetchDepth;

    @Value("${hibernate.query.startup_check}")
    private String queryStartupCheck;

    @Value("${hibernate.show_sql}")
    private String showSql;

    @Value("${hibernate.temp.use_jdbc_metadata_defaults}")
    private String tempUseJdbcMetadataDefaults;

    @Value("${hibernate.use_sql_comments}")
    private String useSqlComments;

    @Value("${hibernate.integration.envers.enabled}")
    private String useEnvers;

    @Value("${javax.persistence.validation.mode}")
    private String validationMode;

    @Value("${hibernate.query.startup_check}")
    private String queryValidation;

    @Value("${hibernate.generate_statistics}")
    private String generateStatistics;

    @Bean
    AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    @Override
    public DataSource actualDataSource() {
        final var dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(jdbcDriverClassName);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(jdbcUser);
        dataSource.setPassword(jdbcPassword);
        return dataSource;
    }

    private DataSource getDataSource() throws SQLException {
        final var creator = new PrettyQueryEntryCreator();
        creator.setMultiline(true);
        final var listener = new SystemOutQueryLoggingListener();
        listener.setQueryLogEntryCreator(creator);

        return ProxyDataSourceBuilder.create(getHikariDataSource())
            .name("ProxyDataSource")
            .countQuery()
            .multiline()
            .listener(listener)
            .logSlowQueryToSysOut(1, TimeUnit.MINUTES)
            .build();
    }

    @Bean
    @Override
    public PlatformTransactionManager transactionManager()
        throws SQLException {
        return new JpaTransactionManager(entityManagerFactory().getObject());
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Override
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    private Map<String, ?> actualPropertiesMap() throws SQLException {
        StandardServiceRegistry standardRegistry = null;
        try {
            final var standardRegistryBuilder = new StandardServiceRegistryBuilder();
            final Map<String, Object> settings = new HashMap<>();
            settings.put(AvailableSettings.DATASOURCE, getDataSource());
            standardRegistryBuilder.applySettings(settings);
            standardRegistry = standardRegistryBuilder.build();
            return settings;
        } catch (final Exception ex) {
            System.err.println(
                "Initial SessionFactory creation failed." + ex.getMessage());
            StandardServiceRegistryBuilder.destroy(standardRegistry);
            throw new ExceptionInInitializerError(ex.getCause());
        }
    }

    private Properties additionalProperties() {
        final var properties = new Properties();
        properties.setProperty("hibernate.cache.use_second_level_cache}", cacheUseSecondLevelCache);
        properties.setProperty("hibernate.cache.use_query_cache", cacheUseQueryCache);
        properties.setProperty("hibernate.cglib.use_reflection_optimizer", cglibUseReflectionOptimizer);
        properties.setProperty("hibernate.connection.charSet", charSet);
        properties.setProperty("hibernate.connection.provider_disables_autocommit", "true");
        properties.setProperty("hibernate.order_inserts", "true");
        properties.setProperty("hibernate.order_updates", "true");
        properties.setProperty("hibernate.query.fail_on_pagination_over_collection_fetch", "true");
        properties.setProperty("hibernate.query.in_clause_parameter_padding", "true");
        properties.setProperty("hibernate.jdbc.time_zone", "UTC");
        properties.setProperty("hibernate.current_session_context_class", currentSessionContextClass);
        properties.setProperty("hibernate.dialect", dialect);
        properties.setProperty("hibernate.format_sql", formatSql);
        properties.setProperty("hibernate.hbm2ddl.auto", hbm2ddl);
        properties.setProperty("hibernate.jdbc.batch_size", jdbcBatchSize);
        properties.setProperty("hibernate.jdbc.fetch_size", jdbcFetchSize);
        properties.setProperty("hibernate.max_fetch_depth", maxFetchDepth);
        properties.setProperty("hibernate.query.startup_check", queryStartupCheck);
        properties.setProperty("hibernate.show_sql", showSql);
        properties.setProperty("hibernate.temp.use_jdbc_metadata_defaults", tempUseJdbcMetadataDefaults);
        properties.setProperty("hibernate.use_sql", "true");
        properties.setProperty("hibernate.use_sql_comments", useSqlComments);
        properties.setProperty("hibernate.physical_naming_strategy",
            "com.vladmihalcea.hibernate.type.util.CamelCaseToSnakeCaseNamingStrategy");

        properties.setProperty("org.hibernate.envers.audit_table_suffix", "_audit");
        properties.setProperty("org.hibernate.envers.modified_flag_suffix", "_mod");
        properties.setProperty("org.hibernate.envers.revision_field_name", "revision_id");
        properties.setProperty("org.hibernate.envers.revision_type_field_name", "revision_type");
        properties.setProperty("org.hibernate.envers.audit_strategy_validity_end_rev_field_name", "revision_end");
        properties.setProperty("org.hibernate.envers.store_data_at_delete", "true");
        properties.setProperty("org.hibernate.envers.audit_strategy_validity_store_revend_timestamp", "true");
        properties.setProperty("org.hibernate.envers.global_with_modified_flag", "true");
        properties.setProperty("hibernate.integration.envers.enabled", useEnvers);

        properties.setProperty("javax.persistence.validation.mode", validationMode);
        properties.setProperty("hibernate.query.startup_check", queryValidation);
        properties.setProperty("hibernate.generate_statistics", generateStatistics);

        return properties;
    }

    @Bean
    @Override
    public LocalContainerEntityManagerFactoryBean entityManagerFactory()
        throws SQLException {
        final var em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName("gexcat");
        em.setDataSource(actualDataSource());
        em.setPackagesToScan(packagesToScan());
        em.setJpaProperties(additionalProperties());
        em.setJpaVendorAdapter(jpaVendorAdapter());
        em.setJpaPropertyMap(actualPropertiesMap());

        return em;
    }

    /**
     * For Blazer
     * 
     * @return
     * @throws SQLException
     */
//    @Bean
//    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
//    @Lazy(false)
//    public CriteriaBuilderFactory createCriteriaBuilderFactory()
//        throws SQLException {
//        final var config = Criteria.getDefault();
//        return config.createCriteriaBuilderFactory(entityManagerFactory().getNativeEntityManagerFactory());
//    }
//
//    @Bean
//    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
//    @Lazy(false)
//    public EntityViewManager createEntityViewManager()
//        throws SQLException, ClassNotFoundException, URISyntaxException {
//        final var cfg = EntityViews.createDefaultConfiguration();
////        for (final var e : FileMisc.scanResources("/com/gexcat/gex/repository/view", "", false)) {
////            final var clazz = Class.forName(e.replace("/", ".").replace(".com", "com").replace(".class", ""));
////            cfg.addEntityView(clazz);
////        }
//        EntityViewManager evm = cfg.createEntityViewManager(createCriteriaBuilderFactory());
//        return evm;
////        return cfg.createEntityViewManager(createCriteriaBuilderFactory());
//    }
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Lazy(false)
    public CriteriaBuilderFactory createCriteriaBuilderFactory(EntityManagerFactory entityManagerFactory) {
        var config = Criteria.getDefault();
        // do some configuration
        return config.createCriteriaBuilderFactory(entityManagerFactory);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Lazy(false)
    // inject the criteria builder factory which will be used along with the entity view manager
    public EntityViewManager createEntityViewManager(CriteriaBuilderFactory cbf, EntityViewConfiguration entityViewConfiguration) {
        var evm = entityViewConfiguration.createEntityViewManager(cbf);
        return evm;
    }
    @Override
    protected String[] packagesToScan() {
        return new String[] { "com.gexcat.gex.jpa.entity", };
    }

    private static class PrettyQueryEntryCreator extends DefaultQueryLogEntryCreator {
        private final Formatter formatter = FormatStyle.BASIC.getFormatter();

        @Override
        protected String formatQuery(final String query) {
            return this.formatter.format(query);
        }
    }

}
