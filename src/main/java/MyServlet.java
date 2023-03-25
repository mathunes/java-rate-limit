
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import utils.RateLimit;

@WebServlet("/MyServlet")
public class MyServlet extends HttpServlet {

	RateLimit rateLimit;

	public MyServlet() {
		if (rateLimit == null) {
			rateLimit = new RateLimit();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if (rateLimit.doFilter(request)) {
			response.setContentType("application/json");
			String json = "{\"status\": \"server running\"}";
			response.getWriter().write(json);
		} else {
			response.setStatus(429);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}