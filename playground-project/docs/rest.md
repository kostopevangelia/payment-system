# REST Configurations
This page explains **how rest api integrations are configured** in the project.

## Beans & Annotations
**Configuration class:**  
`com.evangeliakostop.playground.config.rest/RestConfig.java`
### What this class does
It creates and configures two different RestTemplate clients for calling external HTTP APIs:

one for Stripe (@Qualifier("restTemplateStripe"))

one for your Fraud API (@Qualifier("restTemplateFraudApi"))

Both use Apache HttpClient 5 under the hood (connection pooling, timeouts), plus your custom interceptors/logging.
### RestTemplate Configurations
```java
@Bean
public RestTemplateBuilder restTemplateBuilder() {
    return new RestTemplateBuilder();
}
```
Provides a plain RestTemplateBuilder so we can build RestTemplates in a fluent way.
```java
@Bean
public RequestConfig requestConfig() {
    return RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.ofMilliseconds(5000))
            .setResponseTimeout(Timeout.ofMilliseconds(30000))
            .build();
}
```
Defines timeouts for Apache HttpClient:

connectionRequestTimeout = 5s → how long to wait to borrow a connection from the pool.

responseTimeout = 30s → how long to wait for the server to send a response after the request is sent.
```java
@Bean
public CloseableHttpClient httpClient(final RequestConfig requestConfig) {
    return HttpClientBuilder.create()
            .setConnectionManager(new PoolingHttpClientConnectionManager())
            .setDefaultRequestConfig(requestConfig)
            .build();
}
```
Builds a **CloseableHttpClient** with:

a PoolingHttpClientConnectionManager (reuses TCP connections),

the above requestConfig as default timeouts.
```java
@Bean
@Qualifier("restTemplateStripe")
public RestTemplate restTemplateStripe(final RestTemplateBuilder builder, final CloseableHttpClient httpClient) {

    final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
    factory.setHttpClient(httpClient);
    factory.setConnectTimeout(30000);

    final RestTemplate restTemplate = builder.customizers(new RestLoggingCustomiser(factory, correlationIdInterceptor, new RestInterceptor())).
            build();
    log.info("Generic Spring's RestTemplate Initialized");

    return restTemplate;
}
```
Creates the Stripe RestTemplate:

Wraps the Apache HttpClient in HttpComponentsClientHttpRequestFactory.

Sets an extra connect timeout = 30s (time to establish the TCP connection).

Applies your RestLoggingCustomiser, which (from the name) probably:
sets the request factory,
adds correlationIdInterceptor,
adds a RestInterceptor (e.g., headers/logging).
```java
@Bean
@Qualifier("restTemplateFraudApi")
public RestTemplate restTemplateFraudApi(RestTemplateBuilder builder, final CloseableHttpClient httpClient) {
    CookieStore cookieStore = new BasicCookieStore();
    CloseableHttpClient customHttpClient = HttpClients.custom()
            .setDefaultCookieStore(cookieStore)
            .setDefaultRequestConfig(requestConfig())
            .build();

    final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(customHttpClient);
    factory.setConnectTimeout(30000);

    final RestTemplate restTemplate = builder.customizers(new RestLoggingCustomiser(factory, correlationIdInterceptor, new RestInterceptor()))
            .build();

    log.info("Generic Spring's RestTemplate Initialized for fraud api");

    return restTemplate;
}
```
Creates the Fraud API RestTemplate with cookies enabled:

Builds a separate HttpClient that has a CookieStore (so it can keep cookies across requests if the API uses sessions).

Uses the same default timeouts via requestConfig().

Sets connect timeout = 30s on its request factory.

Applies the same RestLoggingCustomiser (so logging + correlation id still work).
