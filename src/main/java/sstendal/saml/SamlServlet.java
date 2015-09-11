package sstendal.saml;

import com.signicat.services.client.ScResponseException;
import com.signicat.services.client.ScSecurityException;
import com.signicat.services.client.saml.SamlFacade;
import com.signicat.services.client.saml.SamlResponseData;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * Provides an end-point that
 *      - validates any saml token in the request
 *      - establish a server side session if the token is valid
 *      - returns a temporary web page with a script that makes a callback to the angular scope in the ng-signicat directive
 */
public class SamlServlet extends HttpServlet {

    public void init() throws ServletException {
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Retrieves the SAML Response from HTTP Request if it is there
        String assertion = request.getParameter("SAMLResponse");
        if (assertion != null && assertion.length() > 0) {
            Properties configuration = new Properties();
            configuration.setProperty("debug", "false");

            // The name of the certificate we trust.
            // IMPORTANT! This must be changed when moving from test to production.
            boolean useTestEnvironment = true;
            if (useTestEnvironment) {
                configuration.setProperty("asserting.party.certificate.subject.dn", "CN=test.signicat.com/std, OU=Signicat, O=Signicat, L=Trondheim, ST=Norway, C=NO");
            } else {
                configuration.setProperty("asserting.party.certificate.subject.dn", "CN=id.signicat.com/std, OU=Signicat, O=Signicat, L=Trondheim, ST=Norway, C=NO");
            }

            // Creates the SamlFacade object that will be used to parse SAML responses
            SamlFacade samlFacade = new SamlFacade(configuration);

            try {
                // Parse and validate the SAML Request
                SamlResponseData samlResponseData = samlFacade.readSamlResponse(assertion, new URL(request.getRequestURL().toString()));

                // Reads the name identifier from the attribute map
                String username = samlResponseData.getSubjectName();
                if (username == null) {
                    System.err.println("Error: Could not authenticate user. The SAML response did not contain the neccesary information.");
                }

                // Simple mechanism to create a user session
                request.getSession().setAttribute("username", username);

                response.getWriter().write(success);

                return;

            } catch (ScResponseException e) {
                System.err.println("ERROR: The user was not authenticated: " + e.getMessage());
            } catch (ScSecurityException e) {
                System.err.println("ERROR: The login was aborted. Technical message: " + e.getMessage());
            }

        }

        response.getWriter().write(abort);

    }

    public void destroy() {
        // do nothing.
    }


    // A temporary web page with a script that calls the loginSuccess function in the signicat directives scope
    private static final String success = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "\n" +
            "    <link rel=\"stylesheet\" href=\"//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css\"/>\n" +
            "    <link rel=\"stylesheet\" href=\"style.css\"/>\n" +
            "\n" +
            "    <script>\n" +
            "        var scope = parent.angular.element('signicat>iframe').scope();\n" +
            "        // Calls the loginSuccess function in the angular directive for the element named 'signicat'\n" +
            "        scope.loginSuccess();\n" +
            "    </script>\n" +
            "\n" +
            "\n" +
            "</head>\n" +
            "<body>\n" +
            "\n" +
            "Logging in ...\n" +
            "\n" +
            "</body>\n" +
            "</html>";


    // A temporary web page with a script that calls the loginSuccess function in the signicat directives scope
    private static final String abort = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "\n" +
            "    <link rel=\"stylesheet\" href=\"//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css\"/>\n" +
            "    <link rel=\"stylesheet\" href=\"style.css\"/>\n" +
            "\n" +
            "    <script>\n" +
            "        var scope = parent.angular.element('signicat>iframe').scope();\n" +
            "        // Calls the loginAborted function in the angular directive for the element named 'signicat'\n" +
            "        scope.loginAborted();\n" +
            "    </script>\n" +
            "\n" +
            "\n" +
            "</head>\n" +
            "<body>\n" +
            "\n" +
            "Aborting ...\n" +
            "\n" +
            "</body>\n" +
            "</html>";

}
