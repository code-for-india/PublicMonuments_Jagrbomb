# Public Monuments Entry and Surveillance: Team Jagrbomb at Code For India Social Innovation Hackathon at Rastrapathi Bhavan, Delhi, India

**Team Members:**
https://github.com/akhileshdubey
https://github.com/anand2412
https://github.com/ankitaggarwal011
https://github.com/simrankour

**Challenge Name:** 
Monitoring of entry and surveillance in public monuments 

**Problem Statement:** 

Monitoring of entry and surveillance in public monuments which include authentication of visitor, checking the suspicious visitor activities.

**Solution:** 

The project authenticates and manages the entry of visitors into the public monuments and alerts the security in case of any suspicious behavior. The project uses BLE beacons for surveillance and tracking.

The project has three important components:
Visitor Application
Guard Application
Security Dashboard

The visitor applications gets connected with the visitors Aadhar Card/Passport details and allows easy authentication and payment options. The application gets connected with the beacons inside public monuments to track any suspicious activities or entry into prohibited areas and alerts the security personnel through the security dashboard in case of suspicious activities.

The guard applications helps authenticate the visitors using their fingerprints and Aadhar details, and eliminates the need for queues and counters.

The security dashboard provides all the details about the visitors and their activities, alerting the security in case of suspicious behavior. 

**Technologies Used**

Frontend: JavaScript, HTML5, CSS3, Freeboard 
Backend: Firebase as a database service
Hosting: localhost

Android App: Android Studio, Java, Android Sdk
Minimum Android Version Supported: IceCream Sandwitch (Android 4.0)
Beacons (BLE)

# Build Instructions
**NOTE:** Please enter your firebase URL to configure the database service.

**Android App**
**NOTE:** This app requires android studio to build and run
+ [/android](/android) contains the code of android app.
1. `cd android`.
2. Import project in Android Studio (File->New Project->Project From Version Control->Github)
3. Once imported, Click Run-> 'Run App'. You can either connect your device or run in emulator.

**Dashboard**
Copy the dashboard folder into your localhost/server and then open the folder location.