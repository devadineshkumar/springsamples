package com.multidb.transaction_demo.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.atomikos.jdbc.AtomikosDataSourceBean;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.multidb.transaction_demo.h2.repo",
        entityManagerFactoryRef = "h2EntityManager",
        transactionManagerRef = "transactionManager" // use global JTA TM
)
public class H2DataSourceConfig {

    private final Environment env;

    public H2DataSourceConfig(Environment env) {
        this.env = env;
    }

    @Bean(name = "h2DataSource")
    @ConfigurationProperties(prefix = "app.datasource.h2")
    public AtomikosDataSourceBean h2DataSource() {
        JdbcDataSource xa = new JdbcDataSource();
        xa.setURL(env.getProperty("app.datasource.h2.url"));
        xa.setUser(env.getProperty("app.datasource.h2.username"));
        xa.setPassword(env.getProperty("app.datasource.h2.password"));

        AtomikosDataSourceBean xaBean = new AtomikosDataSourceBean();
        xaBean.setUniqueResourceName("h2XA");
        xaBean.setXaDataSource(xa);
        xaBean.setMaxPoolSize(5);
        return xaBean;
    }

    @Bean(name = "h2EntityManager")
    public LocalContainerEntityManagerFactoryBean h2EntityManager(@Qualifier("h2DataSource") DataSource ds) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setJtaDataSource(ds);
        em.setPackagesToScan("com.multidb.transaction_demo.h2.entity");
        em.setPersistenceUnitName("h2PersistenceUnit");
        HibernateJpaVendorAdapter vendor = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendor);
        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.transactionType", "JTA");
        props.put("hibernate.transaction.jta.platform", "org.hibernate.engine.transaction.jta.platform.internal.AtomikosJtaPlatform");
        String dialect = env.getProperty("app.datasource.h2.jpa.hibernate.dialect");
        if (dialect != null) {
            props.put("hibernate.dialect", dialect);
        }
        props.put("hibernate.hbm2ddl.auto", env.getProperty("app.datasource.h2.jpa.hibernate.ddl-auto"));
        props.put("hibernate.show_sql", env.getProperty("app.datasource.h2.jpa.show-sql", "false"));
        em.setJpaPropertyMap(props);
        return em;
    }

}
