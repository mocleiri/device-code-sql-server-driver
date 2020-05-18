package device.code.sql.server.driver;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IClientCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

public class TokenSourceImpl extends AbstractTokenSourceImpl {

    private static final Logger log = LoggerFactory.getLogger(TokenSourceImpl.class);

    private String clientId;

    private String clientSecret;


    public TokenSourceImpl(String tenantId, String clientId, String clientSecret) {
        super(tenantId);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        acquireToken();
    }

    @Override
    protected synchronized final void acquireToken()  {

        try {

            IClientCredential credential = ClientCredentialFactory.createFromSecret(clientSecret);

            ConfidentialClientApplication cca =
                    ConfidentialClientApplication
                            .builder(clientId, credential)
                            .authority(AUTHORITY)
                            .build();

            ClientCredentialParameters parameters =
                    ClientCredentialParameters
                            .builder(SCOPE)
                            .build();

            this.token = cca.acquireToken(parameters).join();
        }
        catch (MalformedURLException e) {
            throw new RuntimeException("unexpected exception on token acquisition: ", e);
        }

    }

}
