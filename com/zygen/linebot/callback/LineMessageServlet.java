package com.zygen.linebot.callback;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.security.core.server.csi.IXSSEncoder;
import com.sap.security.core.server.csi.XSSEncoder;

import com.zygen.linebot.callback.LineMessage;
import com.zygen.linebot.callback.LineMessageDAO;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;
import java.io.BufferedReader;
/**
 * Servlet implementation class LineMessageServlet
 */
public class LineMessageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(LineMessageServlet.class);

	private LineMessageDAO lineDAO;

	/** {@inheritDoc} */
	@Override
	public void init() throws ServletException {
		try {
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");
			lineDAO = new LineMessageDAO(ds);
		} catch (SQLException e) {
			throw new ServletException(e);
		} catch (NamingException e) {
			throw new ServletException(e);
		}
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LineMessageServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().println("<p>Persistence with JDBC!</p>");
		try {
			appendAddForm(response);
			appendMessageTable(response);

		} catch (Exception e) {
			response.getWriter().println("Persistence operation failed with reason: " + e.getMessage());
			LOGGER.error("Persistence operation failed", e);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			doAdd(request);
			doGet(request, response);
		} catch (Exception e) {
			response.getWriter().println("Persistence operation failed with reason: " + e.getMessage());
			LOGGER.error("Persistence operation failed", e);
		}
	}
	
	private void appendMessageTable(HttpServletResponse response)
			throws SQLException, IOException {
		// Append table that lists all persons
		List<LineMessage> resultList = lineDAO.selectAllMessage();
		response.getWriter().println(
				"<p><table border=\"1\"><tr><th colspan=\"3\">"
						+ (resultList.isEmpty() ? "" : resultList.size() + " ")
						+ "Entries in the Database</th></tr>");
		if (resultList.isEmpty()) {
			response.getWriter().println(
					"<tr><td colspan=\"3\">Database is empty</td></tr>");
		} else {
			response.getWriter()
					.println(
							"<tr><th>Header</th><th>Body</th><th>Id</th></tr>");
		}
		IXSSEncoder xssEncoder = XSSEncoder.getInstance();
		for (LineMessage p : resultList) {
			response.getWriter().println(
					"<tr><td>" + xssEncoder.encodeHTML(p.getHeader())
							+ "</td><td>"
							+ xssEncoder.encodeHTML(p.getBody())
							+ "</td><td>" + p.getId() + "</td></tr>");
		}
		response.getWriter().println("</table></p>");
	}
	private void appendAddForm(HttpServletResponse response) throws IOException {
		// Append form through which new persons can be added
		response.getWriter()
				.println(
						"<p><form action=\"\" method=\"post\">"
								+ "Header:<input type=\"text\" name=\"Header\">"
								+ "&nbsp;Body:<input type=\"text\" name=\"Body\">"
								+ "&nbsp;<input type=\"submit\" value=\"Add Message\">"
								+ "</form></p>");
	}
	private void doAdd(HttpServletRequest request) throws ServletException, IOException, SQLException {

		Enumeration<String> headerNames = request.getHeaderNames();
		String header = "";
		if (headerNames != null) {
			while (headerNames.hasMoreElements()) {
				header += request.getHeader(headerNames.nextElement()) + ",";
			}
		}
		String body = getPostData(request) ;

		// Add person if name is not null/empty
		if (header != null && body != null && !header.trim().isEmpty() && !body.trim().isEmpty()) {
			LineMessage lineMessage = new LineMessage();
			lineMessage.setHeader(header.trim());
			lineMessage.setBody(body.trim());
			lineDAO.addLineMessage(lineMessage);
		}
	}
	public static String getPostData(HttpServletRequest req) {
	    StringBuilder sb = new StringBuilder();
	    try {
	        BufferedReader reader = req.getReader();
	        reader.mark(10000);

	        String line;
	        do {
	            line = reader.readLine();
	            sb.append(line).append("\n");
	        } while (line != null);
	        reader.reset();
	        // do NOT close the reader here, or you won't be able to get the post data twice
	    } catch(IOException e) {
	        LOGGER.error("getPostData couldn't.. get the post data", e);  // This has happened if the request's reader is closed    
	    }

	    return sb.toString();
	}

}
