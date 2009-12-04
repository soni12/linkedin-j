/**
 *
 */
package com.google.code.linkedinapi.client.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientException;
import com.google.code.linkedinapi.client.enumeration.HttpMethod;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInApiConsumer;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;

/**
 * @author nmukhtar
 *
 */
public abstract class BaseLinkedInApiClient implements LinkedInApiClient {

    /** Field description */
    private LinkedInAccessToken accessToken;

    /** Field description */
    private LinkedInApiConsumer apiConsumer;

    /**
     * Constructs ...
     *
     *
     * @param consumerKey
     * @param consumerSecret
     */
    protected BaseLinkedInApiClient(String consumerKey, String consumerSecret) {
        apiConsumer = new LinkedInApiConsumer(consumerKey, consumerSecret);
    }

    /**
     * Method description
     *
     *
     * @param apiConsumer
     */
    public void setApiConsumer(LinkedInApiConsumer apiConsumer) {
        this.apiConsumer = apiConsumer;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public LinkedInApiConsumer getApiConsumer() {
        return apiConsumer;
    }

    /**
     * Method description
     *
     *
     * @param accessToken
     */
    public void setAccessToken(LinkedInAccessToken accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Method description
     *
     *
     * @return
     */
    public LinkedInAccessToken getAccessToken() {
        return accessToken;
    }

    /**
     *
     *
     * @param apiUrl
     *
     * @return
     */
    protected LinkedInApiCallResponse callApiMethod(String apiUrl) {
        try {
            LinkedInOAuthService oAuthService =
                LinkedInOAuthServiceFactory.getInstance().createLinkedInOAuthService(apiConsumer.getToken(),
                    apiConsumer.getTokenSecret());
            URL               url     = new URL(apiUrl);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();

            oAuthService.signRequestWithToken(request, accessToken);
            request.connect();

            return new LinkedInApiCallResponse(request.getResponseCode(),
                                               convertStreamToString(request.getInputStream()));
        } catch (Exception e) {
            throw new LinkedInApiClientException(e);
        }
    }

    /**
     *
     *
     * @param apiUrl
     * @param xmlContent
     * @param contentType
     * @param method
     *
     * @return
     */
    protected LinkedInApiCallResponse callApiMethod(String apiUrl, String xmlContent, String contentType,
            HttpMethod method) {
        try {
            LinkedInOAuthService oAuthService =
                LinkedInOAuthServiceFactory.getInstance().createLinkedInOAuthService(apiConsumer.getToken(),
                    apiConsumer.getTokenSecret());
            URL               url     = new URL(apiUrl);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();

            request.setRequestMethod(method.fieldName());
            request.setDoOutput(true);
            oAuthService.signRequestWithToken(request, accessToken);

            if (contentType != null) {
                request.setRequestProperty("Content-Type", contentType);
            }

            if (xmlContent != null) {
                PrintStream out = new PrintStream(request.getOutputStream());

                out.print(xmlContent);
                out.flush();
                out.close();
            }

            request.connect();

            return new LinkedInApiCallResponse(request.getResponseCode(),
                                               convertStreamToString(request.getInputStream()));
        } catch (Exception e) {
            throw new LinkedInApiClientException(e);
        }
    }

    /**
     * Method description
     *
     *
     * @param is
     *
     * @return
     */
    protected String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder  sb     = new StringBuilder();
        String         line   = null;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    /**
     * Class description
     *
     *
     */
    protected static class LinkedInApiCallResponse {

        /** Field description */
        private String responseContent;

        /** Field description */
        private String responseMessage;

        /** Field description */
        private int statusCode;

        /**
         * Constructs ...
         *
         *
         * @param statusCode
         * @param responseContent
         */
        public LinkedInApiCallResponse(int statusCode, String responseContent) {
            this.statusCode      = statusCode;
            this.responseContent = responseContent;
        }

        /**
         * @return the statusCode
         */
        public int getStatusCode() {
            return statusCode;
        }

        /**
         * @param statusCode the statusCode to set
         */
        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        /**
         * @return the responseContent
         */
        public String getResponseContent() {
            return responseContent;
        }

        /**
         * @param responseContent the responseContent to set
         */
        public void setResponseContent(String responseContent) {
            this.responseContent = responseContent;
        }

        /**
         * @return the responseMessage
         */
        public String getResponseMessage() {
            return responseMessage;
        }

        /**
         * @param responseMessage the responseMessage to set
         */
        public void setResponseMessage(String responseMessage) {
            this.responseMessage = responseMessage;
        }
    }
}
