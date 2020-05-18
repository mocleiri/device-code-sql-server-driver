/*
Copyright(c) 2020 Michael O'Cleirigh
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

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class DeviceCodeDriver implements Driver {

    /*
     We make the data static because we only want to require the user to login once.  If the token expires they
     will be asked to login again.

     Typically token expiry is 1 hour.

     */
    private static TokenRefreshingSQLServerConnectionPoolDataSource sqlServerDataSource = null;

    private static TokenSource tokenSource = null;

    @Override
    public synchronized Connection connect(String url, Properties info) throws SQLException {

        if (tokenSource == null) {

            String tenantId = info.getProperty("tenantId");
            String clientId = info.getProperty("clientId");
            String clientSecret = info.getProperty("clientSecret");

            if (clientSecret == null) {
                // login as developer
                tokenSource = new DeveloperTokenSourceImpl(tenantId, clientId, info.getProperty("pathToChrome"));
            }
            else {
                // login as app registration service principal
                tokenSource = new TokenSourceImpl(tenantId, clientId, clientSecret);
            }

            sqlServerDataSource = new TokenRefreshingSQLServerConnectionPoolDataSource(tokenSource);

            sqlServerDataSource.setServerName(info.getProperty("server"));
            sqlServerDataSource.setDatabaseName(info.getProperty("database"));

            sqlServerDataSource.setEncrypt(true);
            sqlServerDataSource.setTrustServerCertificate(false);
            sqlServerDataSource.setHostNameInCertificate(info.getProperty("hostnameInCertificate"));
        }

        return sqlServerDataSource.getConnection();
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return true;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return true;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }
}
