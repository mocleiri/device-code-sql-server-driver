/*
﻿Copyright(c) 2020 Michael O'Cleirigh
All rights reserved.

MIT License
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files(the "Software"),
to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
and / or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions :

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
IN THE SOFTWARE.
 */
package device.code.sql.server.driver;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.aad.msal4j.DeviceCode;
import com.microsoft.aad.msal4j.DeviceCodeFlowParameters;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.InteractiveRequestParameters;
import com.microsoft.aad.msal4j.PublicClientApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class DeveloperTokenSourceImpl extends AbstractTokenSourceImpl {

    private static final Logger log = LoggerFactory.getLogger(DeveloperTokenSourceImpl.class);

    private final String clientId;

    public DeveloperTokenSourceImpl(String tenantId, String clientId) {
        super(tenantId);
        this.clientId = clientId;
        acquireToken();
    }

    @Override
    protected void acquireToken() {

        try {

            PublicClientApplication app = PublicClientApplication.builder(clientId)
                    .authority(AUTHORITY)
                    .build();

            InteractiveRequestParameters parameters = InteractiveRequestParameters
                    .builder(new URI("http://localhost"))
                    .scopes(SCOPE)
                    .build();


            super.token = app.acquireToken(parameters).join();

            if (log.isDebugEnabled())
                printJWTToken("Access", super.token.accessToken());

        }
        catch (Exception e) {
            log.error("unexpected exception when acquiring the token " , e);
        }
    }

    private String printJWTToken(String header, String jwt) {

        StringBuilder sb = new StringBuilder();

        sb.append(header + " - " + jwt);

        if (jwt != null) {

            String accessTokenParts[] = jwt.split("\\.");

            byte[] decodedBytes = Base64.getDecoder().decode(accessTokenParts[1]);

            String decodedString = new String(decodedBytes);

            ObjectMapper mapper = new ObjectMapper();

            try {
                JsonNode tree = mapper.readTree(decodedString);

                sb.append(header + " = " + tree.asText());
                sb.append("\n");

            } catch (JsonProcessingException e) {
                log.error("json processing exception", e);
            }

        }
        return sb.toString();
    }
}
