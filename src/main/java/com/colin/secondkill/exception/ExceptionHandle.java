package com.colin.secondkill.exception;//package com.colin.bh.exception;
//
//import com.colin.bh.exception.email.EmailException;
//import com.colin.bh.exception.user.UserException;
//import com.colin.bh.util.response.ResponseUtil;
//import com.colin.bh.util.response.ServerResponseStatus;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.servlet.ModelAndView;
//
///**
// * {@code @Info}
// *
// * @author 777
// * {@code @date} 2024-03-27
// * {@code @time} 10:49
// */
//@ControllerAdvice
//public class ExceptionHandle {
//
//    @ExceptionHandler(EmailException.class)
//    public ResponseUtil emailExceptionHandle(Exception exception) {
//        String message = exception.getMessage();
//        return new ResponseUtil.ResponseUtilBuilder()
//                .status(ServerResponseStatus.EXCEPTION_ERROR)
//                .message(message)
//                .build();
//    }
//
//    @ExceptionHandler(UserException.class)
//    public ModelAndView userExceptionHandle(Exception exception) {
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("error/500");
//        modelAndView.addObject("message", exception.getMessage());
//        return modelAndView;
//    }
//}
