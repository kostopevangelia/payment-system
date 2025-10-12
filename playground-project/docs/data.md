# Data Access Configurations
This page explains **how database access is configured** in the project.

###Beans & Annotations

**Configuration class:**  
`com.evangeliakostop.playground.config.jdbc/JdbcTemplateConfig.java`

### JdbcTemplate Configurations
```java
@Configuration
public class JdbcTemplateConfig {

    /**
     * Payment-System datasource.
     *
     * @return the data source.
     */
    @Bean("paymentsDbTemplate")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource paymentsDatasource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Jdbc Template.
     *
     * @param dataSource the data source
     * @return the jdbc template
     */
    @Primary
    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("paymentsDbTemplate") final DataSource dataSource) {
        return new JdbcTemplate(dataSource, false);
    }
}
```

## What does this class do

```java 
@Configuration 
```
Tells Spring: “this class creates beans (reusable objects) for the app.”
```java 
@Bean("paymentsDbTemplate")
```
Marked with @Bean("paymentsDbTemplate") so Spring registers a DataSource bean with that name.
```java 
@ConfigurationProperties(prefix = "spring.datasource") 
```
means: read DB settings from application.properties under spring.datasource.* (url, username, password, driver, etc.) and use them to build the DataSource.

In one sentence: connect to the database using whatever you put in spring.datasource.

**jdbcTemplate**

Marked with
```java 
@Bean 
```
and
```java 
@Primary
``` 
It creates the app’s main JdbcTemplate.

It takes the specific DataSource we just made:
```java
@Qualifier("paymentsDbTemplate") 
```
and ensures it uses that DataSource.
```java
@Primary 
```
“if there are multiple JdbcTemplate beans, use this one by default.”
In one sentence: provide a ready-to-use JdbcTemplate that talks to the database above.

That’s it: one bean for the DB connection and one bean for the JdbcTemplate that uses it.

