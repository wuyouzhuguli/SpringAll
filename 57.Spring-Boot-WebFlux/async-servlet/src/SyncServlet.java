import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author MrBird
 */
@WebServlet(urlPatterns = "/sync")
public class SyncServlet extends HttpServlet {

    private static final long serialVersionUID = 7583536145022393360L;

    private Logger log = Logger.getLogger(SyncServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        long start = System.currentTimeMillis();
        this.execute(request, response);
        log.info("总耗时：" + (System.currentTimeMillis() - start) + "ms");
    }

    private void execute(HttpServletRequest request, HttpServletResponse response) {
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
    }
}
