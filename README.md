# The vertical version of DotsPageIndicator
The Android Wear built-in DotsPageIndicator provides an animated array of dots indicating the active current page in horizontal direction *only*.

But a GirdView can be scrolled both vertically and horizontally.

The DotsPageIndicatorVertical make it possible to indicate vertical scrolling pages.

## Import In Android Studio

	repositories {
		url  "http://dl.bintray.com/zhoulujue/maven"
	}

## How To Use

### Exactly the same as Android SDK's DotsPageIndicator

In XML, you can simply declare like this:

        <com.michael.dotspageindicatorvertical.widget.DotsPageIndicatorVertical
            android:id="@+id/launcher_page_indicator"
            android:layout_marginEnd="30dp"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_vertical|end"
            />


# License

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    	
    	http://www.apache.org/licenses/LICENSE-2.0
    	
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
