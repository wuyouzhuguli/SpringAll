/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var Ajax = {
    get: function (url, fn) {
        XR.open("GET", url, true);
        XR.onreadystatechange = function () {
            if (XR.readyState == 4) {
                try {
                    fn(eval("(" + XR.responseText + ")"));
                } catch (e) {
                    fn(null);
                }
            }
        };
        XR.send(null)
    },
    post: function (url, fn) {
        XR.open("POST", url, true);
        XR.onreadystatechange = function () {
            if (XR.readyState == 4) {
                try {
                    fn(eval("(" + XR.responseText + ")"));
                } catch (e) {
                    fn(null);
                }
            }
        };
        XR.send(null)
    },
    put: function (url, fn) {
        XR.open("PUT", url, true);
        XR.onreadystatechange = function () {
            if (XR.readyState == 4) {
                try {
                    fn(eval("(" + XR.responseText + ")"));
                } catch (e) {
                    fn(null);
                }
            }
        };
        XR.send(null)
    },
    delete: function (url, fn) {
        XR.open("DELETE", url, true);
        XR.onreadystatechange = function () {
            if (XR.readyState == 4) {
                try {
                    fn(eval("(" + XR.responseText + ")"));
                } catch (e) {
                    fn(null);
                }
            }
        };
        XR.send(null)
    }
};
var XR = false;
try {
    XR = new XMLHttpRequest()
} catch (trymicrosoft) {
    try {
        XR = new ActiveXObject("Msxml2.XMLHTTP")
    } catch (othermicrosoft) {
        try {
            XR = new ActiveXObject("Microsoft.XMLHTTP")
        } catch (failed) {
            XR = false
        }
    }
}
if (!XR) {
    alert("Error Initializing XMLHttpRequest!")
}
;