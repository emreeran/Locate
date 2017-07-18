Locate
======
An Android location tracking library.

Usage
-----
**Including to your project**

If using Gradle add jcenter or mavenCentral to repositories

        repositories {
            jcenter()
        }
        
Add to your module dependencies:

        dependencies {
            compile 'com.emreeran.locate:locate:0.0.4'
        }
        
To add as a maven dependency:

        <dependency>
          <groupId>com.emreeran.locate</groupId>
          <artifactId>locate</artifactId>
          <version>0.0.3</version>
          <type>pom</type>
        </dependency>
        
**Initialization**

Get Locate instance `Locate locate = Locate.getInstance();` then initialize with desired settings with 

        locate.initialize(this, new Settings.Builder()
                            .interval(10000)
                            .smallestDisplacement(0)
                            .priority(Settings.Priority.HIGH)
                            .build());
                            
**Available settings**

- priority (HIGH, BALANCED, LOW, NO_POWER)
- interval (in ms)
- smallestDisplacement (in meters)
- expirationTime (in ms)
- expirationDuration (in ms)
- fastestInterval (in ms)
- maxWaitTime (in ms)
- numberOfUpdates
- shouldAskPermissions (Default is true)

**Handling permissions**

Locate asks for permissions if shouldAskPermissions is not set as false in the settings, call `Locate.getInstance()
.onRequestPermissionsResult()` in onRequestPermissionsResult of your activity or fragment to handle permission changes like the following

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            Locate.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        }

**Requesting updates**

Start getting updates with a callback
        
        locate.requestLocationUpdates(this, new OnLocationChangedListener() {
            @Override
            public void onLocationChanged(Location location) {
                
            }
        });
        
You can keep updates in the background by calling `locate.requestLocationUpdates(this)` without a listener, this will keep updating last 
location.



To start as a service first create your service class

        public class MyService extends LocateService {
            @Override
            public void onLocationChanged(Location location) {
            
            }
        }
        
Add your service to your manifest inside the application tag

        <service android:name=".MyService"
                 android:exported="false"/>
                 
Start your service like this `locate.startService(this, MyService.class);`

To get the last known location call `locate.getLastLocation()`

To stop location updates use `Locate.stopLocationUpdates()` or if you are running as a service use `stopService(context, MyService.class)`

License
-------

        Copyright 2017 Emre Eran
            
        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at
        
            http://www.apache.org/licenses/LICENSE-2.0
        
        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.