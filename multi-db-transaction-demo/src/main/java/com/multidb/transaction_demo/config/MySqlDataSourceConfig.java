package com.multidb.transaction_demo.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.mysql.cj.jdbc.MysqlXADataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.multidb.transaction_demo.mysql.repo",
        entityManagerFactoryRef = "mysqlEntityManager",
        transactionManagerRef = "transactionManager" // use the global JTA TM provided by Atomikos
)
public class MySqlDataSourceConfig implements EnvironmentAware {

    private Environment env;

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }

    @Primary
    @Bean(name = "mysqlDataSource")
    @ConfigurationProperties(prefix = "app.datasource.mysql")
    public AtomikosDataSourceBean mysqlDataSource() {
        // create XA datasource and wrap in AtomikosDataSourceBean
        MysqlXADataSource xa = new MysqlXADataSource();
        xa.setUrl(env.getProperty("app.datasource.mysql.url"));
        xa.setUser(env.getProperty("app.datasource.mysql.username"));
        xa.setPassword(env.getProperty("app.datasource.mysql.password"));

        AtomikosDataSourceBean xaBean = new AtomikosDataSourceBean();
        xaBean.setUniqueResourceName("mysqlXA");
        xaBean.setXaDataSource(xa);
        xaBean.setMaxPoolSize(10);
        return xaBean;
    }

    @Primary
    @Bean(name = "mysqlEntityManager")
    public LocalContainerEntityManagerFactoryBean mysqlEntityManager(@Qualifier("mysqlDataSource") DataSource ds) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        // use JTA datasource
        em.setJtaDataSource(ds);
        em.setPackagesToScan("com.multidb.transaction_demo.mysql.entity");
        em.setPersistenceUnitName("mysqlPersistenceUnit");
        HibernateJpaVendorAdapter vendor = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendor);
        Map<String, Object> props = new HashMap<>();
        props.put("jakarta.persistence.transactionType", "JTA");
        props.put("hibernate.transaction.jta.platform", "org.hibernate.engine.transaction.jta.platform.internal.AtomikosJtaPlatform");
        // set explicit dialect from application.properties if provided
        String dialect = env.getProperty("app.datasource.mysql.jpa.hibernate.dialect");
        if (dialect != null) {
            props.put("hibernate.dialect", dialect);
        }
        props.put("hibernate.hbm2ddl.auto", env.getProperty("app.datasource.mysql.jpa.hibernate.ddl-auto"));
        props.put("hibernate.show_sql", env.getProperty("app.datasource.mysql.jpa.show-sql", "false"));
        em.setJpaPropertyMap(props);
        return em;
    }

}
