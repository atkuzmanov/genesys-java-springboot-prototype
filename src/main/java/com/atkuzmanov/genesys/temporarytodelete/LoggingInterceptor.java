package com.atkuzmanov.genesys.temporarytodelete;

//@Component
//public class LoggingInterceptor extends HandlerInterceptorAdapter {
public class LoggingInterceptor {

//    private final Logger log = LoggerFactory.getLogger(this.getClass());
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        return true; // Will leave the pre-handling i.e. the request to the logging Aspect.
//    }
//
////    @LogRequestOrResponse
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
////        // called after the controller has processed the request,
////        // so you might log the request here and response
//////        System.out.println("??? TEST 2" + response);
////
////        ContentCachingResponseWrapper wres =
////                new ContentCachingResponseWrapper(
////                        (HttpServletResponse) response);
////
////                ResponseDetailsBuilder rdb = null;
////        try {
////            rdb = ResponseDetails.builder()
////                    .status(response.getStatus())
////    //                .headers(response.get)
////                    .responseBody(getContentAsString(wres.getContentAsByteArray(), response.getCharacterEncoding()));
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////
////        log.info(">>>> OUTGOING_RESPONSE >>>>", fields(rdb.build()));
//    }
//
//    private String getContentAsString(byte[] buf, String charsetName) {
//        if (buf == null || buf.length == 0) {
//            return "";
//        }
//
//        try {
//            int length = Math.min(buf.length, 1000);
//
//            return new String(buf, 0, length, charsetName);
//        } catch (UnsupportedEncodingException ex) {
//            return "Unsupported Encoding";
//        }
//    }
}
