
## PocketAgro - Offline Content with opportunistic synchronization


->![](https://github.com/shantand/PocketAgro/blob/master/images/browse_new.png)<-

PocketAgro is the native android application having Generic UI component. Generic here refers to the fact that we can use exact same layout to consume any other data. In our thesis we have used same technique to produce multiple copies of Apps for different child databases of Agropedia site.


## Functionalities

	1. Reading Articles
	2. Browse
	3. Search
	4. Create new Articles
	5. Peer-to-Peer sharing
	6. Opportunistic Synchronization with Server.

All these functionalities are explained in the [thesis](https://gautam5.cse.iitk.ac.in/opencs/sites/default/files/12111020.pdf). We have used CouchDb(NoSQL Database) for synchronization because of its excellent replication mechanisms.

We also have tools/scripts(built in Python) running as cron job on server that used to pull data from Server databases and update Drupals core so as to be visible for all web clients.

## Known Issues

Though this app is in working condition it will stop working without proper touchdb/sqlite databases in SD card of mobile device. And we can't put up all those data here in public.

## PLEASE DO NOT ASK FOR COMMIT ACCESS.
