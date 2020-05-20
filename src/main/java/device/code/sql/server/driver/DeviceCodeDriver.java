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

import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class DeviceCodeDriver implements Driver {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(DeviceCodeDriver.class);

    /*
     We make the data static because we only want to require the user to login once.  If the token expires they
     will be asked to login again.

     Typically token expiry is 1 hour.

     */
    private static TokenRefreshingSQLServerConnectionPoolDataSource sqlServerDataSource = null;

    private static TokenSource tokenSource = null;

    static {
        System.setProperty("org.slf4j.simpleLogger.logFile", new File(System.getProperty("user.home"), "device-code-sql-server-driver.log").getAbsolutePath());
    }

    private String validateInput(String key, Properties info) {

        String value = info.getProperty(key, "missing " + key);

        String trimmedValue = value.trim();

        log.info("'" + key + "' = '" + trimmedValue  + "'");

        return trimmedValue;

    }
    @Override
    public synchronized Connection connect(String url, Properties info) throws SQLException {

        if (tokenSource == null) {

            String tenantId = validateInput("tenantId", info);
            String clientId = validateInput("clientId", info);
            String clientSecret = validateInput("clientSecret", info);

            if (clientSecret == null) {
                // login as developer
                String pathToChrome = validateInput("pathToChrome", info);

                log.info("login as developer: tenantId={}, clientId={}, pathToChrome={}.", tenantId, clientId, pathToChrome);
                tokenSource = new DeveloperTokenSourceImpl(tenantId, clientId, pathToChrome);
            }
            else {
                // login as app registration service principal
                log.info("login as service principal: tenandId={}, clientId={}, clientSecret={}.");
                tokenSource = new TokenSourceImpl(tenantId, clientId, clientSecret);
            }

            sqlServerDataSource = new TokenRefreshingSQLServerConnectionPoolDataSource(tokenSource);

            sqlServerDataSource.setServerName(validateInput("server", info));
            sqlServerDataSource.setDatabaseName(validateInput("database", info));

            sqlServerDataSource.setEncrypt(true);
            sqlServerDataSource.setTrustServerCertificate(true);
            sqlServerDataSource.setHostNameInCertificate(validateInput("hostnameInCertificate", info));
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
