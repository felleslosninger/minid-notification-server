package no.digdir.minidnotificationserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springdoc.core.SwaggerUiConfigProperties;
import org.springdoc.core.SwaggerUiOAuthProperties;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class SwaggerNonceTranformer extends SwaggerIndexPageTransformer {

    private static final String CSP_NONCE_ATTRIBUTE = "cspNonce";

    public SwaggerNonceTranformer(SwaggerUiConfigProperties swaggerUiConfig, SwaggerUiOAuthProperties swaggerUiOAuthProperties, ObjectMapper objectMapper) {
        super(swaggerUiConfig, swaggerUiOAuthProperties, objectMapper);
    }

    @Override
    public Resource transform(HttpServletRequest request, Resource resource, ResourceTransformerChain transformerChain) throws IOException {
        final AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean isIndexFound = antPathMatcher.match("**/swagger-ui/**/index.html", resource.getURL().toString());

        if (isIndexFound && hasDefaultTransformations()) {
            String html = defaultTransformations(resource.getInputStream());
            String nonce = request.getAttribute(CSP_NONCE_ATTRIBUTE).toString();
            String htmlWithNonce = html
                    .replace("<script", "<script nonce='" + nonce + "'")
                    .replace("<style", "<style nonce='" + nonce + "'")
                    ;
            return new TransformedResource(resource, htmlWithNonce.getBytes());
        } else {
            return resource;
        }
    }
}
