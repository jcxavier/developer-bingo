Developer Bingo
===============

Android application to meet other people with similar technical skills in the form of a bingo game.
Bump with other Android phones to trade skills.

Winning entry at [Google DevFest Portugal 2013].

Video and screenshots to come very soon!

##  Base concept

The Developer Bingo concept was brought by the Google DevFest Portugal organisation, by asking people to name 3 programming languages and 3 frameworks in which they are skilled and display them as a Bingo card.

Participants would then proceed to meet with others with similar skills and if there is a match in one skill, both participants would be able to mark in their card that particular skill and write down information about the other participant (name / occupation).

## Idea

Why not push the concept further? The user information can be polled from Google+, the skills can be inserted dynamically (as many as the user wants, with a minimum of 6), and then a Bingo card can be randomly generated with 6 of the previously inserted skills.

People can then bump their phones and, if there is a match in the skills, the contact of the other person pops up with the newly gained skill, with the option of following to his/her Google+ profile. The card is then marked for both persons and they can now go and find more people with similar skills!


Have you heard of Bump™? Read more about how it works here: [http://bu.mp/company/faq].

### Keep It Static, Stupid

The solution is fully static since its development time was approx. 15h. The original idea involved a NFC solution instead of Bump™, since it is viable offline as well. However, it was impossible to conceive at the time due to the lack of availability of NFC-powered devices.

The original idea also featured a server counterpart, with the following functionalities:

  - Parametrization of the size of Bingo cards (allow cards of a value different than 6 entries);
  - Parametrization of the allowed programming languages / framework lists;
  - Sessions! Connect to a particular Bingo session using a randomly server generated code;
  - Real-time leaderboard of the Bingo session, with metrics such as the distinct number of people connected to and how close they are to scoring a Bingo;
  - Server-side detection of B-I-N-G-O with fancy sounds and lights.


## Technology

  - [Android];
  - [Bump™];
  - [Google Play Services]: Google+ integration.

## Future Work

  - Fix nasty bugs (like app not closing and currently crashing on a Bingo event);  
  - Support both NFC and Bump™ technologies;
  - Develop the server-side and make the application fully dynamic.


-------------------------


##### LICENSE

Copyright [2013] [João Xavier]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.


##### DISCLAIMER

This is a very unstable version, doesn't cope well with orientation changes and might not work properly in some phones. It will get better over time.

[Android]: http://developer.android.com
[Bump™]: http://bu.mp/company/api
[Google Play Services]: https://developers.google.com/+/mobile/android/
[http://bu.mp/company/faq]: [http://bu.mp/company/faq]
[Google DevFest Portugal 2013]: http://gdgpt.blogspot.pt/p/blog-page.html
