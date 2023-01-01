# Bus Express [Android]
---

## Project Description
---

!["Homepage"](Homepage.png "Image of the Default Homepage")
**Bus Express** is a simple, no-frills application that allows users to query details about incoming buses in Singapore by using LTA Datamall's API. The Privacy Policy can be found at the bottom for your persual. A quick guide is also written on the front page of the app.
The application is hosted in the Google Play Store, and can be found here:

## Content
---

1. Queries for the arrival timings, alongside a few metadata information about the incoming 3 buses.
!["Unexpanded Search Results"](`Unexpanded Search Results.png` "Image of search results before interaction")

By providing the bus stop code, which uniquely identifies all the bus stops in Singapore, Bus Express is able to return the desired bus stop as shown above. 
By clicking the left icon, the user retrieves all the necessary information about incoming buses as shown below.
By clicking the right icon, the user can choose to add this particular bus stop into their favourites list.

!["Search Results"](`Search Results.png` "Image of search results after interaction")

As the picture shows, the user is provided all the bus services that passes by the requested bus stop. They can then see the estimated arrival timings, in minutes, of the next three buses. In addition, users will know the type of bus that is coming, such as a bendy or a double decker bus. The wheelchair icon tells users if the incoming bus is wheelchair accessible, as not all bus models have this capability.
The coloured bar below provides a visual representation of the occupancy rates of the buses, with 3 possible states:
* Green - Seating is available
* Orange - Standing space is available
* Red - Limited standing space is available

The right icon has also changed from a "More Options" icon to a "Refresh" icon, allowing users to update the timings for that particular bus stop.

2. Adding the frequently accessed bus stops to your favourites list.
!["Adding to Favourites"](AddFavourites.png "Image of adding favourites feature")

As mentioned earlier, clicking the "More Options" icon would allow users to add the bus stop to their favourites list. Users are provided with 2 options as shown, either "Going Out" or "Coming Back". Regardless of the option chosen, the bus stop will be added to their favourites. The difference is your favourites list can be divided into 2, allowing you to put the bus stops that matches your journey into 1 of the 2, making it more accessible and convenient to locate bus stops.

!["Favourites List"](Favourites.png "Image of favourites list")

After adding a bus stop to your favourites, users can navigate to them using the left navigation bar. They can click the 2 tabs on top to decide which favourites list they wish to access. As you can see, clicking any of the bus stops in your favourites will retrieve the same information as before.

3. Retrieving the bus routes of any bus service number.
!["Bus Routes"](`Bus Routes Timing.png`  "Image of a bus's bus route")

Alternatively, users may choose to provide a bus service number instead of the bus stop code. This would return the bus route, which is a list of all the bus stops that this particular bus service would travel to. Clicking any of the bus stops in the route would also yield the same information as mentioned. There are also 2 tabs at the top, which represents the 2 possible direction a bus service will travel in. Switch between the 2 tabs to find your bus stop. If the bus service is a loop like 962, there will only be 1 tab on top.

4. Dark Mode
!["Dark Mode"](DarkMode.png "Image of Dark Mode")

Although dark mode has been implemented on the application, it is still very rough around the edges as the color contrast for certain composables like icons are bad. There is also no dedicated button to switch between the 2 themes currently. This means that the application will simply follow the theme of your phone. If your phone is in dark mode, it will be launched in the dark mode and vice versa.

## Additional Features (Yet to be Implemented)
---

* Dedicated dark mode button.
* Nearby feature that shows you the nearby bus stops if you permit the app to use your current location.
* Google authentication features to allow cloud backups of your favourite list.
* Ez-link card checking capabilities that allow you to check your balance on the go, so long as the phone has NFC capabilities.
* Street view.
* Announcements about road and traffic breakdown.
* Other QOL enhancements to improve the UI of the application.

## Learning Outcomes
---

* Programming concepts in Kotlin such as collections
* Object oriented programming concepts
* Setting up of Android Studio & Debugger
* Installing application for testing purposes on an emulator and a physical Android smartphone
* Jetpack Compose to build the frontend of the application
* Implementing user interactions through event-based programming
* Understanding state in compose
* Writing automated tests
* Writing unit tests
* Scrolling and Navigating between screens and views
* Changing and implementing application icons and boot screens for varying screen resolutions and firmware versions.
* Material theming in Compose, by customising shape, colour, typography, and even the status bar
* Simple animations in Jetpack Compose
* Implementing and working with Talkback to implement accessibility features for all types of users
* Activity lifecycle of an application
* Logging for debugging purposes
* Android App Architecture & UI Layer
* Implementing ViewModels & StateFlows in Android for configuration changes
* Coroutines in Kotlin
* Retrofit to connect application to backend servers
* Exception handling
* Serialisation of JSON
* Android permissions like the Internet
* Basic dependency injections & Groovy for altering build files when necessary
* Room APIs for data persistence locally
* CRUD Operations with Room APIs for SQL databases
* Review of releases in Play Console and subsequent release of application

## Video Link
---


## Application Privacy Policy
---

**Privacy Policy**

Xuannie built the Bus Express SG app as a Free app. This SERVICE is provided by Xuannie at no cost and is intended for use as is.

This page is used to inform visitors regarding my policies with the collection, use, and disclosure of Personal Information if anyone decided to use my Service.

If you choose to use my Service, then you agree to the collection and use of information in relation to this policy. The Personal Information that I collect is used for providing and improving the Service. I will not use or share your information with anyone except as described in this Privacy Policy.

The terms used in this Privacy Policy have the same meanings as in our Terms and Conditions, which are accessible at Bus Express SG unless otherwise defined in this Privacy Policy.

**Information Collection and Use**

For a better experience, while using our Service, I may require you to provide us with certain personally identifiable information. The information that I request will be retained on your device and is not collected by me in any way.

The app does use third-party services that may collect information used to identify you.

Link to the privacy policy of third-party service providers used by the app

*   [Google Play Services](https://www.google.com/policies/privacy/)

**Log Data**

I want to inform you that whenever you use my Service, in a case of an error in the app I collect data and information (through third-party products) on your phone called Log Data. This Log Data may include information such as your device Internet Protocol (“IP”) address, device name, operating system version, the configuration of the app when utilizing my Service, the time and date of your use of the Service, and other statistics.

**Cookies**

Cookies are files with a small amount of data that are commonly used as anonymous unique identifiers. These are sent to your browser from the websites that you visit and are stored on your device's internal memory.

This Service does not use these “cookies” explicitly. However, the app may use third-party code and libraries that use “cookies” to collect information and improve their services. You have the option to either accept or refuse these cookies and know when a cookie is being sent to your device. If you choose to refuse our cookies, you may not be able to use some portions of this Service.

**Service Providers**

I may employ third-party companies and individuals due to the following reasons:

*   To facilitate our Service;
*   To provide the Service on our behalf;
*   To perform Service-related services; or
*   To assist us in analyzing how our Service is used.

I want to inform users of this Service that these third parties have access to their Personal Information. The reason is to perform the tasks assigned to them on our behalf. However, they are obligated not to disclose or use the information for any other purpose.

**Security**

I value your trust in providing us your Personal Information, thus we are striving to use commercially acceptable means of protecting it. But remember that no method of transmission over the internet, or method of electronic storage is 100% secure and reliable, and I cannot guarantee its absolute security.

**Links to Other Sites**

This Service may contain links to other sites. If you click on a third-party link, you will be directed to that site. Note that these external sites are not operated by me. Therefore, I strongly advise you to review the Privacy Policy of these websites. I have no control over and assume no responsibility for the content, privacy policies, or practices of any third-party sites or services.

**Children’s Privacy**

These Services do not address anyone under the age of 13. I do not knowingly collect personally identifiable information from children under 13 years of age. In the case I discover that a child under 13 has provided me with personal information, I immediately delete this from our servers. If you are a parent or guardian and you are aware that your child has provided us with personal information, please contact me so that I will be able to do the necessary actions.

**Changes to This Privacy Policy**

I may update our Privacy Policy from time to time. Thus, you are advised to review this page periodically for any changes. I will notify you of any changes by posting the new Privacy Policy on this page.

This policy is effective as of 2023-01-01

**Contact Us**

If you have any questions or suggestions about my Privacy Policy, do not hesitate to contact me at xuanyi1603@gmail.com.

This privacy policy page was created at [privacypolicytemplate.net](https://privacypolicytemplate.net) and modified/generated by [App Privacy Policy Generator](https://app-privacy-policy-generator.nisrulz.com/)