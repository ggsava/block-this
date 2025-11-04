# IMPORTANT: SCAMMERS HAVE ASSUMED ACCESS OVER THE DOMAINS: https://www.block-this.com/ and https://savageorgiev.com/ (my personal site).
## After discontinuing the project, scammers have taken hold of my old domain names and are pretending to be myself. I have filed complaints with domain registrars, hosters and the law enforcement, but in the meanwhile be careful!

 
Block This - a DNS based Ad Blocker for Android ( Abandoned )
==========================
<img src="https://block-this.com/static/images/block-this-ad-blocker-logo-small.png" align="right" />


Copyright (C) 2015-2017 Sava Georgiev. This program is free software.

You can redistribute it and/or modify
it under the terms of the GNU General Public License version 3.0.

For more information on Licensing - please read the License file located in the repository.

# Summary
**Block This** is a DNS/VPN based ad blocking & privacy app for Android, that blocks malicious or unwanted internet content by filtering DNS requests and redirecting them to `127.0.0.1 (localhost)`. It uses a dummy VPN client in order to go around the restrictions in Android and change the DNS settings without having root access. The backend behind the app is a DNS based on [PowerDNS](https://www.powerdns.com/) - an open source DNS management software working with [MySQL](https://mysql.com) as a datasource.  

# Contribution
If you wish to join the development please drop in a comment at https://forum.block-this.com or email me at me@savageorgiev.com.

Alternatively, you can just make a pull request here.

# Installation

To get the latest release of **Block This**, go to:

  https://block-this.com

The main logic that blocks the host names is contained within your own DNS server. You can setup one using PDNS (https://www.powerdns.com/). Once this is done, add the following in your app/src/main/assets/config.properties file.
For testing the app you can use any valid DNS address (e.g. 8.8.8.8, 8.8.4.4) but ofcourse using a public DNS will not block any ads.
~~~
dns1=YOUR_DNS1_IP_ADDRESS
dns2=YOUR_DNS2_IP_ADDRESS
~~~
If you wish to run it locally and want to use fabric (https://fabric.io/) for issue tracking you will have to create your own app/fabric.properties file containing the following (optional):
~~~
apiSecret=YOUR_FABRIC_API_SECRET
apiKey=YOUR_FABRIC_API_KEY
~~~
In order for "Games" support section from the menu to work you can register with AppNext (https://www.appnext.com) and get an Api Key. Once this is done, add it to your app/src/main/assets/config.properties file (optional):
~~~
appNextApiKey=YOUR_APP_NEXT_API_KEY
~~~

# Community

If you wish to join the conversation, please visit our official forums at:

  https://forum.block-this.com

or the Google+ community at:

  https://plus.google.com/u/0/communities/113434074801403849306

# About

I created the app for my own convenience as I needed a privacy and ad blocking solution for my Android device, which I did not want to root. The app worked so well for me that I decided to put it on the Play Store. It had a huge success with almost 1 million downloads in just 2 months, but then it was taken down by Google. It is now hosted on my website as an APK. I open source it with the hope to get further developed and supported by the community.

# Contact 

For any questions or feedback you can contact me at https://savageorgiev.com or email me at me@savageorgiev.com
