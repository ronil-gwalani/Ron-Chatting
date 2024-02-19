

# Android Chat Library : Simplified Chatting for Android Developers

Firebase Chat Library for Android is a powerful and easy-to-use library that enables real-time chatting between two users in Android applications. It is built on top of Firebase Realtime Database and Firebase Firestore, providing seamless integration and robust functionality for developers.

1. **Real-time messaging: Send and receive messages instantly between two users.** 
2. **Message history: View message history between users**

3. **Easy integration: Simple and intuitive API for easy integration into Android apps** 

4. **Configuration Flexibility:** Customizing of the color combinations of the chat screens is also there.

5. **User-friendly Design:** The library's user interface is designed to be intuitive and user-friendly.



## Step 1. Add the JitPack repository to your build file
Add the following code to your build.gradle file at the project level:

```kotlin
allprojects {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```

## Step 2. Add the dependency

  **Add Dependency:** 
 1. in build.gradel add classpath dependency in your project level code

```kotlin

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
    }

}
                      

````

 2. in build.gradel add google service Plugin  in module level code

```kotlin

plugins {
     // ...
    id 'com.google.gms.google-services'
}
                      
````

In your app-level build.gradle file, add the dependency for the Chatting library:
```kotlin
dependencies {
    implementation 'com.github.ronil-gwalani:Ron-Chatting:v1.0.1'
}
```

## Step 3. Firebase Setup:

 1.Connect Firebase to your Android project and add the generated google-services.json file.

 2.Enable the Realtime Database and Cloud Firestore for chatting functionality.

 3. Create a class named FCMService extending FirebaseMessagingService()

```kotlin
class FCMService : FirebaseMessagingService() {

    private val ronChatting by lazy{ RonChattingUtils(this//context) }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        ronChatting.newTokenGenerated(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (ronChatting.isFcmChattingPayload(remoteMessage.data)) {
            ronChatting.manageNotifications(remoteMessage.data)
            return
        }
    }
}
````
4. Register the service in your AndroidManifest.xml:

```xml
<service
    android:name=".services.FCMService"
    android:enabled="true"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
````

## Step 4. User Registration:
1. Create a RonChattingUserModel object containing user information:
````kotlin
val user = RonChattingUserModel(
    userID = "yourUniqueUserId",
    userName = "userName",
    fcmToken = "yourFcmToken",
    profileImage = "https://i0.wp.com/www.smartprix.com/bytes/2023/05/2-photoutils.com_.jpg?ssl=1&quality=80&w=f"//can be null
)
````
2. Register the user using RonChattingUtils:
````kotlin
ronChatting.register(user, "Your Firebase Server Key", object : UserRegisterCallbacks {
    override fun onUserRegistered(model: RonChattingUserModel) {
        // User registered successfully
    }

    override fun onUserRegistrationFailed(error: String?) {
        // Handle registration error
    }
})
````

## Step 5. Starting a Chat:
1. Call startChatting with the target user ID:
````kotlin
  RonChattingUtils(context).startChatting("targetedId")
````
2. (Optional) Provide a unique node for chat persistence:
if you want every time the two same user get connected there previous chats must not be visible you just need to pass a unique Node which has to be unique every time 
[this case scenario arises in the situation of cab booking where by luck the same user and driver get connected with another ride so there previous chats must not be visible that's why in this kind of situation you can pass that unique code there booking id or something to make it different]
````kotlin
 RonChattingUtils(context).startChatting("targetedId", "uniqueNode")
````
3. (Optional) Use a callback to handle process events:

````kotlin
 RonChattingUtils(context).startChatting(binding.etTargetID.text.toString(), callback = object:ChattingResponseCallback{
                override fun onProcessStarted() {
                    Log.d("onProcessStarted", ": ", )// here you will be notified as so as you triger the startChatting methord so here you can start the actions like showing progress bar 
                }

                override fun onErrorFound(error: String?) {  // if you found any error from database or anything this methord will be trigered
                    Log.d("onErrorFound", ": ", )
                }

                override fun onProcessCompleted() {
                    Log.d("onProcessCompleted", ": ", )// once all the things are complted and the chatting screen is about to be started then this methored will be called so that you can peform the actions like stoping the progress bar
                }
            })
````


## Customizations:
1. you can customized the chat screen colors and background just need to override these in your color.xml

````xml
    <!--       For Customization   -->
    <color name="secondaryColor">#DABB52</color>-->
    <color name="hintColor">#FFFFFFFF</color>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
    <color name="textColor">#FFFFFFFF</color>
    <color name="receiverChatBoxColor">#304FFE</color>
    <color name="senderChatBoxColor">#FFD600</color>
    <color name="receiverTextColor">#FFFFFFFF</color>
    <color name="senderTextColor">#000000</color>
    <color name="backgroundColor">#FFFFFFFF</color>
    <color name="messageBoxTextColor">#070707</color>
    <color name="messageBoxHintColor">#404040</color>
    <color name="messageBoxBackgroundColor">#F8F7F7</color>
    <color name="sendMessageIconColor">#000000</color>

````

**if you want to customized the chat screen background you have need to add a file in your drawable with the name 
ron_chat_screen_bg.xml**


With these steps, you should be able to successfully integrate and use the Chatting library in your project. If you encounter any issues or have further questions, feel free to reach out.

## Contributing

Contributions to Ron Chatting Library are welcome! If you find any issues or have suggestions for improvements, feel free to open an issue or submit a pull request.

## License

 Ron Chatting Library  Library is released under the **MIT License**. See the [LICENSE](https://en.wikipedia.org/wiki/MIT_License) file for more details.

## Support

For any questions or support related to Chatting Library, you can reach out to us at ronilgwalani@gmail.com or join my community forum.

## Credits

The Chatting Picker Library library was developed by [Ronil Gwalani](https://github.com/ronil-gwalani) feel free to contact in case of any query.
