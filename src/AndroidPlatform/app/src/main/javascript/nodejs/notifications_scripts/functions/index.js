'use strict'
/* eslint-disable */

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

/**
*
* Reference: https://www.youtube.com/watch?v=I6p-kYOALbE&list=PLxefhmF0pcPmtdoud8f64EpgapkclCllj&index=53
* This JS script is deployed to Firebase cloud functions. It activates when a user requests a connection with another user.
*
**/
exports.sendNotifications = functions.database.ref('/Notifications/Requests/{receiver_user_id}/{notification_id}').onWrite((data,context) =>
{
    const receiver_user_id = context.params.receiver_user_id;
    const notification_id = context.params.notification_id;
    console.log('The user Id is : ', receiver_user_id);

    console.log('A notification has been recorded in the logs{SENT TO}:=', receiver_user_id);

    if(!data.after.exists())
    {
        return console.log('A Notification has been deleted from the database : ', notification_id);
    }

    if(!data.after.val())
    {
        console.log('A notification has been deleted:', notification_id);
        return null;
    }

    const sender_user_id = admin.database().ref(`/Notifications/Requests/${receiver_user_id}/${notification_id}`).once('value');

    return sender_user_id.then(fromUserResult =>
    {
        const from_sender_user_id = fromUserResult.val().author;

        console.log('You have a notification:' , sender_user_id);

        const userQuery = admin.database().ref(`/Users/${from_sender_user_id}/first_name`).once('value');

        return userQuery.then(userResult =>
        {
            const sender_user_name = userResult.val();
            const DeviceToken = admin.database().ref(`/Users/${receiver_user_id}/device_token`).once('value');

            return DeviceToken.then(result =>
            {
                const token_id = result.val();
                const payload =
                {
                    notification:
                    {
                        from_sender_user_id : from_sender_user_id,
                        title: "Contact Request",
                        body: `${sender_user_name} wants to connect with you.`,
                        icon: "default"
                    }
                };

                return admin.messaging().sendToDevice(token_id, payload).then(response =>
                {
                    console.log('This was a notification feature for New Requests.')
                    return;
                }).catch(error =>
                {
                    console.error(error);
                    res.error(500);
                });
            });
        });
    });
});

/**
*
* This JS script is deployed to Firebase cloud functions. It activates when a new route is added to the dashboard. All users receive a notification.
*
**/
exports.sendNewRouteNotifications = functions.database.ref('/Notifications/Routes/{sender_user_id}/{notification_id}')
.onWrite(async (event,context) =>
{
    const sender_user_id = context.params.sender_user_id;
    const notification_id = context.params.notification_id;
    console.log('The user Id is : ', sender_user_id);

    console.log('A notification has been recorded in logs: Message is: ', data);

    if(!data.after.exists())
    {
        return console.log('A Notification has been deleted from the database : ', sender_user_id);
    }

    if(!data.after.val())
    {
        console.log('A notification has been deleted:', sender_user_id);
        return null;
    }

    return sender_user_id.then(fromUserResult =>
    {
        const from_sender_user_id = fromUserResult.val().author;

        console.log('You have a notification:' , sender_user_id);

        const userQuery = admin.database().ref(`/Users/${from_sender_user_id}/first_name`).once('value');

        return userQuery.then(userResult =>
        {
            const sender_user_name = userResult.val();
            const DeviceToken = admin.database().ref(`/Users/${receiver_user_id}/device_token`).once('value');

            return DeviceToken.then(result =>
            {
                const token_id = result.val();
                const payload =
                {
                    notification:
                    {
                        from_sender_user_id : from_sender_user_id,
                        title: "New Message",
                        body: `${sender_user_name} has sent you a new message`,
                        icon: "default"
                    }
                };

                return admin.messaging().sendToDevice(token_id, payload).then(response =>
                {
                    console.log('This was a notification feature for New Messages.')
                    return;
                }).catch(error =>
                {
                    console.error(error);
                    res.error(500);
                });
            });
        });
    });


});

/**
*
* This JS script is deployed to Firebase cloud functions. It activates when a message is sent from one user to another.
*
**/
exports.sendMessageNotifications = functions.database.ref('/Notifications/Messages/{receiver_user_id}/{notification_id}').onWrite((data,context) =>
{
    const receiver_user_id = context.params.receiver_user_id;
    const notification_id = context.params.notification_id;
    console.log('The user Id is : ', receiver_user_id);

    console.log('A notification has been recorded in logs: Message is: ', data);

    if(!data.after.exists())
    {
        return console.log('A Notification has been deleted from the database : ', sender_user_id);
    }

    if(!data.after.val())
    {
        console.log('A notification has been deleted:', sender_user_id);
        return null;
    }

    const sender_user_id = admin.database().ref(`/Notifications/Messages/${receiver_user_id}/${notification_id}`).once('value');

    return sender_user_id.then(fromUserResult =>
    {
        const from_sender_user_id = fromUserResult.val().author;

        console.log('You have a notification:' , sender_user_id);

        const userQuery = admin.database().ref(`/Users/${from_sender_user_id}/first_name`).once('value');

        return userQuery.then(userResult =>
        {
            const sender_user_name = userResult.val();
            const DeviceToken = admin.database().ref(`/Users/${receiver_user_id}/device_token`).once('value');

            return DeviceToken.then(result =>
            {
                const token_id = result.val();
                const payload =
                {
                    notification:
                    {
                        from_sender_user_id : from_sender_user_id,
                        title: "New Message",
                        body: `${sender_user_name} has sent you a new message`,
                        icon: "default"
                    }
                };

                return admin.messaging().sendToDevice(token_id, payload).then(response =>
                {
                    console.log('This was a notification feature for New Messages.')
                    return;
                }).catch(error =>
                {
                    console.error(error);
                    res.error(500);
                });
            });
        });
    });
});

/**
*
* This JS script is deployed to Firebase cloud functions. It activates by sending a message to the owner of a post when the post is viewed.
*
**/
exports.sendPostViewNotifications = functions.database.ref('/Notifications/Views/{receiver_user_id}/{notification_id}').onWrite((data,context) =>
{
    const receiver_user_id = context.params.receiver_user_id;
    const notification_id = context.params.notification_id;
    console.log('The user Id is : ', receiver_user_id);

    console.log('A notification has been recorded in logs: Message is: ', data);

    if(!data.after.exists())
    {
        return console.log('A Notification has been deleted from the database : ', sender_user_id);
    }

    if(!data.after.val())
    {
        console.log('A notification has been deleted:', sender_user_id);
        return null;
    }

    const sender_user_id = admin.database().ref(`/Notifications/Views/${receiver_user_id}/${notification_id}`).once('value');

    return sender_user_id.then(fromUserResult =>
    {
        const from_sender_user_id = fromUserResult.val().author;
        const route_id = fromUserResult.val().route;
        console.log('You have a notification:' , sender_user_id);

        const userQuery = admin.database().ref(`/Routes/${route_id}/route_end`).once('value');

        return userQuery.then(userResult =>
        {
            const short_addr = userResult.val();
            const DeviceToken = admin.database().ref(`/Users/${receiver_user_id}/device_token`).once('value');

            return DeviceToken.then(result =>
            {
                const token_id = result.val();
                const payload =
                {
                    notification:
                    {
                        from_sender_user_id : from_sender_user_id,
                        title: "New Post View",
                        body: `Someone has viewed your post with destination: ${short_addr}`,
                        icon: "default"
                    }
                };

                return admin.messaging().sendToDevice(token_id, payload).then(response =>
                {
                    console.log('This was a notification feature for New Post Views.')
                    return;
                }).catch(error =>
                {
                    console.error(error);
                    res.error(500);
                });
            });
        });
    });
});
