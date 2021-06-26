package com.igramassistant.app.domain.igram.services;

import com.igramassistant.app.interfaces.GenricDataCallback;
import com.igramassistant.app.utl;

import java.io.IOException;
import java.util.HashMap;

import fi.iki.elonen.NanoHTTPD;
import lombok.Getter;
import lombok.Setter;

public class HttpWebService extends NanoHTTPD {

    @Getter
    @Setter
    GenricDataCallback callback;
    @Setter
    AutomatorService automatorService;

    public HttpWebService() {
        super(8080);
        callback = (message, statusCode) -> {

        };
    }

    @Override
    public Response serve(IHTTPSession session) {
        String answer = "";
        try {
            String uri = session.getUri();
            final HashMap<String, String> map = new HashMap<String, String>();
            session.parseBody(map);
            final String payload = map.get("postData");
            answer = "not found";
            if (uri.equals("/runjs")) {
                callback.onStart(payload, AutomatorService.RUN_JS);
                answer = "ok";
            } else if (uri.equals("/loadpage") || uri.equals("/loadurl")) {
                if (session.getParameters().get("url") != null)
                    callback.onStart(session.getParameters().get("url").get(0), AutomatorService.LOAD_URL);
                else
                    callback.onStart(payload, AutomatorService.LOAD_URL);
                answer = "ok";
            }
            else if (uri.equals("/evaluate")) {
               automatorService.evaluate(payload);
               answer = "ok";
            }
            else if (uri.equals("/geteval")) {
                answer = automatorService.getEval();
            }
        } catch (IOException | ResponseException ioe) {
            utl.e("Httpd", ioe.toString());
        }

        return newFixedLengthResponse(answer);
    }

}