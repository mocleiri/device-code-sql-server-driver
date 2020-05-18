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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class DeviceCodeDirverTest {
    @Test void testSomeLibraryMethod() {

        try {
            // I created a dummy html file in the users home directory before calling this.
            File landingPage = new File(new File(System.getProperty("user.home")), "device-code-login.html");


            Process p = Runtime.getRuntime().exec("\"/Program Files (x86)/Google/Chrome/Application/chrome.exe\" --app file:///" + landingPage.getAbsolutePath());

            System.out.println("Google Chrome launched!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
