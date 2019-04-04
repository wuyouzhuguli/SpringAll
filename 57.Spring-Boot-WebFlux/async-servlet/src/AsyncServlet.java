import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author MrBird
 */
@WebServlet(urlPatterns = "/async", asyncSupported = true)
public class AsyncServlet extends HttpServlet {
    private static final long serialVersionUID = 393375716683413545L;

    private Logger log = Logger.getLogger(AsyncServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        long start = System.currentTimeMillis();
        AsyncContext asyncContext = request.startAsync();

        CompletableFuture.runAsync(() -> execute(asyncContext, asyncContext.getRequest(), asyncContext.getResponse()));
        log.info("总耗时：" + (System.currentTimeMillis() - start) + "ms");
    }

    private void execute(AsyncContext asyncContext, ServletRequest request, ServletResponse response) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            response.getWriter().append("hello");
        } catch (IOException e) {
            e.printStackTrace();
        }
        asyncContext.complete();
    }
}
