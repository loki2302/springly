package me.loki2302.springly;

import me.loki2302.springly.playground.MyController;
import me.loki2302.springly.playground.web.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DummyTest {
    private RequestProcessor<ControllerHandler, ControllerParameterMetadata, ControllerRequest> controllerRequestProcessor;

    @Before
    public void setUp() {
        ControllerMetadataReader controllerMetadataReader = new ControllerMetadataReader();
        HandlerReader<ControllerClassMetadata, ControllerMethodMetadata, ControllerParameterMetadata, ControllerHandler> classReader =
                new HandlerReader<ControllerClassMetadata, ControllerMethodMetadata, ControllerParameterMetadata, ControllerHandler>(
                        controllerMetadataReader);

        HandlerPredicate<ControllerHandler, ControllerRequest> handlerPredicate = new ControllerHandlerPredicate();
        HandlerRegistry<ControllerHandler, ControllerRequest> controllerHandlerRegistry =
                new HandlerRegistry<ControllerHandler, ControllerRequest>(handlerPredicate);

        HandlerMethodArgumentResolverRegistry<ControllerParameterMetadata, ControllerRequest> handlerMethodArgumentResolverRegistry =
                new HandlerMethodArgumentResolverRegistry<ControllerParameterMetadata, ControllerRequest>();
        handlerMethodArgumentResolverRegistry.register(new PathParamHandlerMethodArgumentResolver());
        handlerMethodArgumentResolverRegistry.register(new QueryParamHandlerMethodArgumentResolver());

        controllerRequestProcessor =
                new RequestProcessor<ControllerHandler, ControllerParameterMetadata, ControllerRequest>(controllerHandlerRegistry, new HandlerInstanceResolver() {
                    @Override
                    public Object resolveInstance(Class<?> handlerClass) {
                        return new MyController();
                    }
                }, handlerMethodArgumentResolverRegistry);

        List<ControllerHandler> controllerHandlers = classReader.readClass(MyController.class);
        controllerHandlerRegistry.register(controllerHandlers);
    }

    @Test
    public void canCallActionOne() {
        ControllerRequest controllerRequest = new ControllerRequest();
        controllerRequest.path = "/api/1";
        controllerRequest.pathParams = Collections.<String, Object>singletonMap("x", 123);
        controllerRequest.queryParams = Collections.<String, Object>singletonMap("y", "hello");
        Object result = controllerRequestProcessor.processRequest(controllerRequest);
        assertEquals("123 hello", result);
    }

    @Test
    public void canCallAddNumbers() {
        ControllerRequest controllerRequest = new ControllerRequest();
        controllerRequest.path = "/api/addNumbers";
        controllerRequest.pathParams = new HashMap<String, Object>() {{
            put("x", 2);
            put("y", 3);
        }};

        controllerRequest.queryParams = Collections.<String, Object>singletonMap("y", "hello");
        Object result = controllerRequestProcessor.processRequest(controllerRequest);
        assertEquals(5, result);
    }
}
