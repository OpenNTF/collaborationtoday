Collaboration Today DOTS-FeedMonster Project

DOTS Task for the CollaborationToday project gets configured feed entries into CT database.

FeedMonster uses Rome library to read feed entries. Because of OSGi incompatibilities in Rome library, the first version wasn't running as a scheduled DOTS tasklet.

In the second version, thanks to Nathan T.Freeman from Red Pill Development, we have converted Rome library to an OSGi plugin and made a number of correction in the source code. Modified and original version of the source code are included in the repository.

Refer to installation.txt for details about how to install and use FeedMonster.
