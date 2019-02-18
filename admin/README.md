Securing the admin app:

- the admin app ui itself is protected with form login using the credentials specified in the properties file.
    - we could use spring security 5 with web flux security configurations instead of servlet security.
      the reason I didn't is I was experiment with stuff that didn't work with flux security
- the admin app also needs to access protected urls:
    - we need something that can stay authenticated in background to collect statistics and alerting
    types of protections:
    1- resources that are protected with *jwt token* (gateway)
        - I basically needed to use client credentials to access protected actuators using oauth2.
          we have a bean HttpHeadersProvider that does this on startup.

        notes / previous attempts worth mentioning :
        - I tried using oauth2login client which basically makes admin secured by same oauth2 server but that didn't make sense
        since I needed  the token at startup (not when someone tries to access a url),
        - we could use oauth2 to protect the admin app ui itself, but that doesn't mean we should use the same token passed
          to access actuators because we need the app to gain access anytime.

    2- oauth2 server with *basic auth* protection
        - spring protects oauth2 endpoints with basic auth
        - we get the instance of oauth2 through eurka, and in eurka we store the instance metadata including the credentials
        - the admin app automatically picks up those and uses them to authenticate it's requests to the auth server

    3- behind the gateway services (identity, food etc), unprotected
        - those are not protected using spring security and not accessible using a public url
        - admin has no problem getting to actuator endpoint