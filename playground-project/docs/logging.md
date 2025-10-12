# Logging Configurations


## Server-side Logging
This page explains **how server-side logging is configured** in the project.
### What we have implemented
#### CachingFilter

*What it is:* A Servlet filter that wraps the request & response so their bodies can be read multiple times.

*Why:* By default the body streams are one-time-read. If you log them, the controller wouldn’t see them. This filter buffers them so both logging and your controller can read the body.

*In one line:* Make request/response bodies replayable so we can log them.

#### CorrelationIdFilter

*What it is:* A Servlet filter that ensures every request has a correlation id.

*How:* If the inbound header (e.g. X-Correlation-Id) exists → use it; else generate a new id. It typically puts it in MDC so all logs include it.

*In one line:* Attach/propagate a correlation id to the request and to the logging context.

#### WebConfig

*What it is:* Your Spring MVC configuration class.

*What it does:* Registers the RequestResponseLoggingInterceptor and (often) the filters, and can set include/exclude paths.

*In one line:* Wire the interceptor/filter into Spring so they actually run.

#### RequestResponseLoggingInterceptor

*What it is:* A Spring MVC HandlerInterceptor that logs what came in and what went out.

*Logs:* HTTP method, path, status, duration, and—if enabled—request/response body, headers, etc.
(Thanks to CachingFilter, it can read the body without breaking things.)

*When it runs:* preHandle (before controller), afterCompletion (after the response).

*In one line:* Log each HTTP call to the app (and bodies if configured)

##### preHandle
This is the “before the controller” hook of the interceptor.
<ul>
<li>preHandle(...) runs before the controller method is called.</li>
<li>return true; means let the request continue to the controller.</li>
<li>If you ever return false;, Spring stops the chain (controller won’t run)</li>
</ul>

```java
@Override
public boolean preHandle(HttpServletRequest request, 
                         HttpServletResponse response, 
                         Object handler) {
    return true;
}
```
##### postHandle
*When it runs:* after the controller method finishes, before the response is written out.

*Guard:* it only runs for real controller methods (handler instanceof HandlerMethod).

*What it extracts:*
<ul>
<li>controllerName and methodName from the handler (e.g., PaymentController#create).</li>
<li>Request body from ContentCachingRequestWrapper (so it can read the body safely). If the request wasn’t wrapped, it logs an empty string.</li>
<li>Client IP using X-Forwarded-For if present, else request.getRemoteAddr().</li>
<li>All headers as a single string.</li>
</ul>
*What it logs:* one line like
“➡️ Entry to service from <Controller> and method <method>: Client IP: <ip>, Headers: <headers>, Request is: <body>”.

```java
@Override
public void postHandle(HttpServletRequest request, 
                       HttpServletResponse response, 
                       Object handler,
                       ModelAndView modelAndView) throws IOException {
    if (handler instanceof HandlerMethod) {
        HandlerMethod method = (HandlerMethod) handler;

        String controllerName = method.getBeanType().getSimpleName();
        String methodName = method.getMethod().getName();

        ContentCachingRequestWrapper wrappedRequest = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        String requestBody = wrappedRequest != null
                ? new String(wrappedRequest.getContentAsByteArray(), request.getCharacterEncoding())
                : "";

        String clientIp = getClientIpAddress(request);
        String headers = getHeaders(request);

        log.info("➡️ Entry to service from {} " +
                        "and method {}: " +
                        "Client IP: {}, " +
                        "Headers: {}, " +
                        "Request is: {}",
                controllerName, methodName, clientIp, headers, requestBody);
        }
}
```

##### afterCompletion

*When it runs:* after everything is done (controller finished, view rendered or body produced). It’s the last interceptor hook.

*What it checks:* only act if the handler is a real controller method (HandlerMethod).

*What it reads & logs:*
<ul>
<li>Finds the controller name and method name.</li>
<li>Tries to get the cached response body from ContentCachingResponseWrapper.</li>
<li>Logs one line like:✅ Exiting service from {Controller} and method {Method}: Response is: {body} </li>
</ul>
*Why copyBodyToResponse() is critical:*
After you read the cached bytes for logging, you must write them back to the real HTTP stream.

wrappedResponse.copyBodyToResponse(); sends the body to the client.
If you forget this, the client may get an empty response.

```java
@Override
public void afterCompletion(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler,
                            Exception ex) throws IOException {
    if (handler instanceof HandlerMethod) {
        HandlerMethod method = (HandlerMethod) handler;

        String controllerName = method.getBeanType().getSimpleName();
        String methodName = method.getMethod().getName();

        ContentCachingResponseWrapper wrappedResponse = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        String responseBody = wrappedResponse != null
                ? new String(wrappedResponse.getContentAsByteArray(), response.getCharacterEncoding())
                : "";

        log.info("✅ Exiting service from {} " +
                        "and method {}: " +
                        "Response is: {}",
                controllerName, methodName, responseBody);

        if (wrappedResponse != null) {
            wrappedResponse.copyBodyToResponse();
        }
    }
}
```

## Client-side Logging
This page explains **how client-side logging is configured** in the project.
### What we have implemented
#### CachedResponse
It wraps a ClientHttpResponse and buffers its body bytes so they can be read multiple times.

All methods delegate to the original response, except getBody() which returns a new stream over the cached bytes.
#### CorrelationIdInterceptor
*What it does:* ensures every outgoing REST call carries a correlation id so logs across services can be tied together.

*How:* it gets X-Correlation-Id from the logging context (MDC) or creates one, stores it in MDC, and adds it to the request headers before executing the call.

#### RestInterceptor

##### Before the call:
logs the HTTP method, URL and the request body.

Runs the call and measures how long it took with a StopWatch.

##### After the call:
reads the entire response body into memory, logs:

a line with timestamp, a tag (OUTB_CALL), URL, status code, and duration (ms)

another line with method, URL, response body.

#### RestLoggingCustomiser

##### What this class does
<ul>
    <li>It customizes a RestTemplate before you use it.</li>
    <li>It sets a BufferingClientHttpRequestFactory so the request/response bodies can be read multiple times by interceptors (needed for logging).</li>
    <li>It adds your logging interceptor (RestInterceptor) to the RestTemplate.</li>
</ul>