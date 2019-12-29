LINK: https://github.com/okraisan/insecure-signup

The project will open in NetBeans IDE 11.2 as is, and can be started normally,
like any of the exercises. It is a web application that runs in port 8080 on localhost.



FLAW 1:
A3:2017-Sensitive Data Exposure

This web application serves its contents over an unencrypted HTTP connection.
When a user accesses the site over an untrusted link - for example, on public
WiFi or via routers that are known or suspected to eavesdrop - all communication will be in
plain text. Any passwords sent to the server or any sensitive data sent back
to the client are transmitted in the clear and can be inspected by a third
party. A third party could also modify the data in transit since there's no cryptographic
proof of its integrity.

An SSL certificate is required for setting up HTTPS. There are different types
of certificates available, but a free one from Let's Encrypt or similar is enough
to set up encryption.

Then, a URL rewrite rule should be put in place that redirects all users who are
trying to access the HTTP site to the HTTPS version. 



FLAW 2:
A2:2017-Broken Authentication

The list of signed up people should only be accessible to an administrator. However,
the authentication is flawed; it is only based on a URL parameter with no password
authentication. Anyone inspecting the URL can change admin=false to admin=true and
suddenly get access to the list of people. This seems like an outrageous decision
on part of the developers, but "authentication" schemes like this have been seen
in the past, at least anecdotally; the original story can be found by searching for
"dailywtf admin=false".

The URL parameter should be replaced with a proper authentication scheme, preferably based on
sending a password that is then verified against a hashed and salted version of the correct
password on the server side. No login information should
be sent in the URL parameter; instead, the contents of a login form should be
transmitted in an encrypted POST request. The decision of whether someone is
an administrator or not should be left to the server.



FLAW 3:
A6:2017-Security Misconfiguration

The error message displayed when a normal user tries to access the "list" page is too
verbose. It reveals the (arguably obvious) fact that the URL parameter "admin" being "false"
is why the user couldn't access the information. This gives a malicious user a hint about
how to circumvent the authentication, as described in FLAW 2.

Error messages displayed to users should be configured to contain a minimal amount of
information, perhaps just an error code that could be used when reporting the error to
support, for example. In addition to jeopardizing security, detailed information about
what part of the system failed and why can be confusing to ordinary users.

This particular error message is written verbatim in the listPeople() function in
SignupController.java and could simply be changed to say something else.



FLAW 4:
A7:2017-Cross-Site Scripting (XSS)

The form page is supposed to display the name of the event that the user is signing up for. This is
directly copied from the URL parameter named "event". In fact, anyone could modify the parameter
to include JavaScript on the page which would then be executed by a user's browser.
The user sees this as though the script was originating from the web app, like any other scripts
on the page. This could be exploited by tricking a user into visiting a specially crafted URL pointing
to this form, possibly transmitting sensitive user information to an external site or performing
any action on the user's part.

This type of XSS can be mitigated by escaping certain characters in strings before displaying them to
the user, so that the web browser renders them as text instead. At least the "less than" sign should
be escaped. Another way would be to fetch the name of the event from the server and not use the URL
parameter at all.



FLAW 5:
A10:2017-Insufficient Logging & Monitoring

The web server does not write the access log anywhere and doesn't even display it in
the standard output. The access log is where information about all HTTP requests is
written: timestamps, originating IP addresses, requested URLs, and some HTTP header fields as
well. Access logs could be used in investigating a security incident or attempted breach.
They can be useful in debugging any kind of software malfunction as well.

In Apache Tomcat, access logging is disabled by default. It can be enabled by adding
"server.tomcat.accesslog.enabled=true" to application.properties. The project template
doesn't have this file, but it can be created in the src/main/resources directory and
it will be automatically applied to the server.

By default, access logs will be written to a temporary folder.