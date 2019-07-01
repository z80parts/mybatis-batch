package sample;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@MapperScan(basePackages = {
		"mybatis.batch.mapper.h2" }, sqlSessionFactoryRef = "h2SqlSessionFactory")
public class H2DbConfiguration {

	static final private String H2_DATASOURCE_PROPERTIES = "h2DataSourcePropertie";
	static final private String H2_DATASOURCE = "DateSource";
	static final public String H2_TX_MANAGER = "h2TxManager";
	static final private String H2_SQL_SESSION_FACTORY = "h2SqlSessionFactory";

	@Bean(name = H2_DATASOURCE_PROPERTIES)
	@Primary
	@ConfigurationProperties("spring.datasource.h2")
	public DataSourceProperties dataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean(name = H2_DATASOURCE)
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource.h2")
	public DataSource createDataSource(
			@Qualifier(H2_DATASOURCE_PROPERTIES) DataSourceProperties properties) {
		return properties.initializeDataSourceBuilder().build();
	}

	@Bean(name = H2_TX_MANAGER)
	@Primary
	public PlatformTransactionManager createTxManager(
			@Qualifier(H2_DATASOURCE) DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean(name = H2_SQL_SESSION_FACTORY)
	@Primary
	public SqlSessionFactory getSqlSessionFactoryBean(
			@Qualifier(H2_DATASOURCE) DataSource dataSource) throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		ResourcePatternResolver resolver = ResourcePatternUtils
				.getResourcePatternResolver(new DefaultResourceLoader());
		sqlSessionFactoryBean.setConfigLocation(
				resolver.getResource("classpath:mybatis-config.xml"));
		sqlSessionFactoryBean.setMapperLocations(resolver
				.getResources("classpath:mybatis/batch/mapper/h2/*.xml"));
		return sqlSessionFactoryBean.getObject();
	}

//	@Bean(name = "h2SqlSessionTemplate")
//	public SqlSessionTemplate createSqlSessionTemplate(
//			@Qualifier("h2SqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
//		return new SqlSessionTemplate(sqlSessionFactory);
//	}

}
