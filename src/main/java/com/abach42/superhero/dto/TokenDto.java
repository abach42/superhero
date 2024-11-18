package com.abach42.superhero.dto;

/*
 * https://datatracker.ietf.org/doc/html/rfc6750#section-4
 * 
 * 
   Typically, a bearer token is returned to the client as part of an
   OAuth 2.0 [RFC6749] access token response.  An example of such a
   response is:

     HTTP/1.1 200 OK
     Content-Type: application/json;charset=UTF-8
     Cache-Control: no-store
     Pragma: no-cache

     {
       "access_token":"mF_9.B5f-4.1JqM",
       "token_type":"Bearer",
       "expires_in":3600,
       "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA"
     }
 */
public record TokenDto(String access_token, String token_type, int expires_in, String refresh_token) {}