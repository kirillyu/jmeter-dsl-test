package com.mechanitis.demo.junit5;

import static us.abstracta.jmeter.javadsl.JmeterDsl.*;
import us.abstracta.jmeter.javadsl.http.DslHttpSampler;
import org.eclipse.jetty.http.MimeTypes.Type;

public class Samplers {

    static class Sampler {
        
        private final String label;
        private final String path;
        private String body;
        private Type type;

        protected Sampler(String label, String path) {
            this.label = label;
            this.path = path;
        }

        protected Sampler(String label, String path, String body, Type type) {
            this.label = label;
            this.path = path;
            this.body = body;
            this.type = type;
        }

        protected DslHttpSampler get(String host) {
            if(this.body != null && !this.body.trim().isEmpty()) {
                return httpSampler(this.label, host+this.path).post(this.body,this.type);
            } else {
                return httpSampler(this.label, host+this.path);
            }
        }
    }

    public static Sampler ShowcaseList = new Sampler(
                    "_POST  /showcases/list",
                    "/showcases/list",
                    "{ \"coordinates\":{ \"latitude\": ${cur_lat},\"longitude\": ${cur_lon}},\"source\": \"1\"}",
                    Type.APPLICATION_JSON
                    );
    public static Sampler GetShowcaseById = new Sampler(
                    "_GET /showcases/{showcaseId}",
                    "/showcases/${SHOWCASE_ID}?source=1&version=0"
                    );

    public static Sampler ProductSearch = new Sampler(
                    "POST /products/search",
                    "/products/search",
                    "{ \"productSetInfos\":[{\"productSetId\": \"23d2a667-a127-4d6c-a241-54338f01ede8\"}], \"query\": \"${query}\" }",
                    Type.APPLICATION_JSON
                    );
    public static Sampler ProductDictionary = new Sampler(
                    "POST products/dictionary ",
                    "products/dictionary",
                    "[ ${PRODUCT_PAYLOAD} ]",
                    Type.APPLICATION_JSON
                    );
    public static Sampler ProductBatch = new Sampler(
                    "POST /productSets/{productSetId}/products/batch",
                    "/productSets/23d2a667-a127-4d6c-a241-54338f01ede8/products/batch?version=1",
                    "[  ${PRODUCT_PAYLOAD} ]",
                    Type.APPLICATION_JSON
                    );
}
