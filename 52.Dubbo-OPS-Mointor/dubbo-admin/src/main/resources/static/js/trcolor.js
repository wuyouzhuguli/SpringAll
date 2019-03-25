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

// JavaScript Document
// JavaScript Document
window.onload = function showtable() {
    try {
        var tablename = document.getElementById("table_o");
        var li = tablename.getElementsByTagName("tr");
        for (var i = 1; i < li.length; i++) {
            if (i % 2 == 0)
                li[i].style.backgroundColor = "#f5f5f5";
            else
                li[i].style.backgroundColor = "#ffffff";
            li[i].onmouseover = function () {
                this.style.backgroundColor = "#fff9b7";
            }
            li[i].onmouseout = function () {
                if (this.rowIndex % 2 == 0)
                    this.style.backgroundColor = "#f5f5f5";
                else
                    this.style.backgroundColor = "#ffffff";
            }
        }
    } catch (e) {

    }
}





