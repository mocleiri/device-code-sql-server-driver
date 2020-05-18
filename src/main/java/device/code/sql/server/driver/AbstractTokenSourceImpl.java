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

import com.microsoft.aad.msal4j.IAuthenticationResult;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractTokenSourceImpl implements TokenSource {

    protected final String AUTHORITY;

    protected final static Set<String> SCOPE = new HashSet<>();

    static {
      SCOPE.add("https://database.windows.net//.default");
    }

    protected IAuthenticationResult token;

    public AbstractTokenSourceImpl(String tenantId) {
        this.AUTHORITY =  "https://login.microsoftonline.com/"+tenantId+"/";
    }

    protected abstract void acquireToken();

    @Override
    public synchronized final String getAccessToken() {

        if (this.token == null) {
            // refresh the token
            acquireToken();
        }
        else {

            LocalDateTime expiresOnDateTime =
                    this.token.expiresOnDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            if (expiresOnDateTime.isBefore(LocalDateTime.now().minusMinutes(5))) {
                acquireToken();
            }
        }

        return token.accessToken();
    }
}
