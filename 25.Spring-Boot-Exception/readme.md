#springboot 异常处理
###throw HttpMediaTypeNotSupportedException
`@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
     @ExceptionHandler(Exception.class)
     public Map<String, Object> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
     }`
可以获取
因为这个方法处理exception类型错误，也可接受一个HttpMediaTypeNotSupportedException参数

`@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
     @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
     public Map<String, Object> handleHttpMediaTypeNotSupportedException(Exception e) {
     }`
也可以接收。同理
上述两个方法同时存在，@ExceptionHandler(HttpMediaTypeNotSupportedException.class)所在方法被调用
###throw Excepiton
上述两个错误处理器都不可以接受
`@ExceptionHandler(Exception.class)
    public Map<String, Object> handleUserNotExistsException(Exception e) {
    }`
这个可以接收错误
    
