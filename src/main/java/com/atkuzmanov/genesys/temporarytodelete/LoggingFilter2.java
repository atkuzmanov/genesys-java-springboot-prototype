package com.atkuzmanov.genesys.temporarytodelete;

//@Component
//@Order(-3)
//public class LoggingFilter implements Filter {
//public class LoggingFilter implements Filter{
public class LoggingFilter2 {
//
//    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//    }
//
//
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response,
//                         FilterChain chain) throws IOException, ServletException {
//
//        try {
//            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//            chain.doFilter(httpServletRequest, httpServletResponse);
//
//            System.out.println(">>> req: " + httpServletRequest);
//            System.out.println(">>> resp: " + httpServletResponse);
//
//                    ContentCachingResponseWrapper wres = wrapResponse(httpServletResponse);
////                new ContentCachingResponseWrapper(
////                        (HttpServletResponse) response);
//
//                ResponseDetailsBuilder rdb = null;
//
//        try {
//
//
//            rdb = ResponseDetails.builder()
//                    .status(httpServletResponse.getStatus())
//    //                .headers(response.get)
////                    .responseBody(getContentAsString(wres.getContentAsByteArray(), response.getCharacterEncoding()));
////                    .responseBody(new String(wres.getContentAsByteArray()));
//                    .responseBody(new String(wres.getContentInputStream().readAllBytes(), wres.getCharacterEncoding()));
//
//
//            wres.copyBodyToResponse();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        log.info(">>>> OUTGOING_RESPONSE >>>>", fields(rdb.build()));
//
//
//        } catch (Throwable a) {
//            log.error(a.getMessage());
//        }
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
//
//    private static ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
//        if (response instanceof ContentCachingResponseWrapper) {
//            return (ContentCachingResponseWrapper) response;
//        } else {
//            return new ContentCachingResponseWrapper(response);
//        }
//    }
}




