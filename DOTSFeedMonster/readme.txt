Collaboration Today DOTS-FeedMonster Project

DOTS Task for the CollaborationToday project gets configured feed entries into CT database.

Initial version uses Rome library to read feed entries. Because of OSGi incompatibilities in Rome library, it currently does not support scheduled DOTS operations. So to schedule working, you might use Program configuration in names.nsf.

To set up the CT database, you might either use Constants.java or notes.ini variable "Monster_TargetDB" which points to the nsf file (relative path to the data folder).