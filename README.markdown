gather-camel
============
This features uses Apache Camel to specify processing routes for
submitted form data.


Building
--------
`mvn clean install`


Running
-------
`mvn pax:provision`


Configuring
-----------
In the top-level project pom you'll notice some runtime properties
for configuring http and email.


Dependencies
------------
* [gather-commons](http://github.com/akollegger/gather-commons/)
* [gather-archiver](http://githhub.com/akollegger/gather-archiver/)
* [gather-alert](http://github.com/akollegger/gather-alert/)


References
----------
* [Apache James](http://james.apache.org/) - full featured Java mail server

