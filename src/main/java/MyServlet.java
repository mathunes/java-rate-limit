
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
		rateLimit = new RateLimit();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String json = "";
		response.setContentType("application/json");

		if (rateLimit.doFilter(request)) {
			json = "{\"message\": \"ok\"}";
			response.setStatus(200);			
		} else {
			json = "{\"message\": \"too many requests\"}";
			response.setStatus(429);
		}

		response.getWriter().write(json);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}