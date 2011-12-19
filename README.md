SVNADMIN
========

An easy tool developed using play! framework for management Collabnet Subversion access rules.
Insert users, groups and repositories and the svnadmin tool creates a svn_access_file used by Collabnet.


Features
---------------

* Management users and groups
* Create subversion repositories
* Define repository access rules
* Data stored in database (default is MySQL)


Instalation
---------------

1. Download and install the play! framework (www.playframework.com) in /opt and add /opt/play directory in the $PATH.
2. Run the build.sh script to generate the svnadmin.war file.
3. Now you can deploy the svnadmin.war in several servers (tomcat for example).


How to contrib?
---------------

Run play eclipsify to generate an eclipse project or play netbeansify for netbeans project.
In eclipse, you just need to import the project. Click on File\Import\General\Existing Projects into Workspace
