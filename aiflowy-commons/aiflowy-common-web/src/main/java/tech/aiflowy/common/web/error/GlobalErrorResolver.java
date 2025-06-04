package tech.aiflowy.common.web.error;

import tech.aiflowy.common.domain.Result;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.alibaba.fastjson.support.spring.annotation.FastJsonView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GlobalErrorResolver implements HandlerExceptionResolver {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalErrorResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ex.printStackTrace();
        Result error;
        if (ex instanceof MissingServletRequestParameterException) {
            error = Result.fail(1, ((MissingServletRequestParameterException) ex).getParameterName() + " 不能为空.");
        } else if (ex instanceof NotLoginException) {
            error = Result.fail(99, "请登录");
        } else if (ex instanceof NotPermissionException || ex instanceof NotRoleException) {
            error = Result.fail(1, "无权操作");
        } else {
            LOG.error(ex.toString(), ex);
            error = Result.fail(1, "错误信息：" + ex.getMessage());
        }

        return new ModelAndView(new FastJsonView())
                .addAllObjects(error);
    }
}
