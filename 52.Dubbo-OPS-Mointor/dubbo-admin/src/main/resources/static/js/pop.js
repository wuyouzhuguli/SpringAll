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
$(function () {

    function showModal(src, height, width) {
        jQuery.modal('<iframe src="' + src + '" height="' + height + '" width="' + width + '" frameborder="0" allowTransparency=true>', {
            closeHTML: "<input type='button' style='display:none'/>",
            closeClass: "modalClose",
            opacity: 35,
            overlayCss: {
                backgroundColor: "#000"
            }
        });
    }

    $(".addnamespace").click(function () {
        showModal("addnamespace.html", "630", "600");
    });

    function megaHoverOver() {
        $(this).find(".sub").stop().fadeTo('fast', 1).show();
        //Calculate width of all ul's
        (function ($) {
            jQuery.fn.calcSubWidth = function () {
                rowWidth = 0;
                //Calculate row
                $(this).find("ul").each(function () {
                    rowWidth += $(this).width();
                });
            };
        })(jQuery);

        if ($(this).find(".row").length > 0) { //If row exists...
            var biggestRow = 0;
            //Calculate each row
            $(this).find(".row").each(function () {
                $(this).calcSubWidth();
                //Find biggest row
                if (rowWidth > biggestRow) {
                    biggestRow = rowWidth;
                }
            });
            //Set width
            $(this).find(".sub").css({'width': biggestRow});
            $(this).find(".row:last").css({'margin': '0'});
        } else { //If row does not exist...
            $(this).calcSubWidth();
            //Set Width
            $(this).find(".sub").css({'width': rowWidth});
        }
    }

    function megaHoverOut() {
        $(this).find(".sub").stop().fadeTo('fast', 0, function () {
            $(this).hide();
        });
    }

    var config = {
        sensitivity: 1, // number = sensitivity threshold (must be 1 or higher)
        interval: 100, // number = milliseconds for onMouseOver polling interval
        over: megaHoverOver, // function = onMouseOver callback (REQUIRED)
        timeout: 200, // number = milliseconds delay before onMouseOut
        out: megaHoverOut // function = onMouseOut callback (REQUIRED)
    };
});