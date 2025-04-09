package tech.aiflowy.common.web.jsonbody;

import tech.aiflowy.common.util.RequestUtil;
import com.mybatisflex.core.util.ConvertUtil;
import com.mybatisflex.core.util.StringUtil;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class JsonBodyArgumentResolver implements HandlerMethodArgumentResolver, SmartInitializingSingleton {

    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;


    public JsonBodyArgumentResolver(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
    }

    @Override
    public void afterSingletonsInstantiated() {
        List<HandlerMethodArgumentResolver> argumentResolvers = requestMappingHandlerAdapter.getArgumentResolvers();
        ArrayList<HandlerMethodArgumentResolver> resolvers = new ArrayList<>(Objects.requireNonNull(argumentResolvers));
        resolvers.add(0, this);
        requestMappingHandlerAdapter.setArgumentResolvers(resolvers);
    }


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JsonBody.class);
    }


    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer
            , NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        JsonBody jsonBody = parameter.getParameterAnnotation(JsonBody.class);
        Class<?> paraClass = parameter.getParameterType();

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            return null;
        }

        Object jsonObjectOrArray = RequestUtil.readJsonObjectOrArray(request);

        Object result = null;
        Type paraType = parameter.getGenericParameterType();
        if (paraType instanceof TypeVariable) {
            Type variableRawType = JsonBodyParser.getTypeVariableRawType(
                    parameter.getContainingClass(), ((TypeVariable<?>) paraType));
            if (variableRawType != null) {
                paraClass = (Class<?>) variableRawType;
                paraType = variableRawType;
            }
        }
        try {
            result = JsonBodyParser.parseJsonBody(jsonObjectOrArray, paraClass, paraType, jsonBody.value());
        } catch (Exception e) {
            if (jsonBody.skipConvertError()) {
                //ignore
            } else {
                throw new IllegalArgumentException(e.getMessage(), e);
            }
        }

        if (result == null && StringUtil.hasText(jsonBody.defaultValue())) {
            result = ConvertUtil.convert(jsonBody.defaultValue(), paraClass);
        }

        if ((result == null) && jsonBody.required()) {
            throw new IllegalArgumentException(jsonBody.value() + " must not be null or blank");
        }

        return result;
    }


}
