gather-camel - xforms route
===========================


Live Testing
------------
The sample xforms data can be submitted to a running gather-camel
using the command line utility `curl` like this:

    curl --verbose --data @src/test/resources/xforms/data/simple.xml \  
         --header "Content-Type: text/xml" \  
         --header "Content-Encoding: UTF-8" \  
         http://localhost:8080/example/spi


Or, if you happen to be running gather-sling, something like this:

    curl --verbose --data @src/test/resources/xforms/data/simple.xml \  
        --header "Content-Type: text/xml" \  
        --header "Content-Encoding: UTF-8" \  
        http://localhost:8090/gather/rosa/data.xml

Obviously, the URL should be altered to reflect the current runtime reality. 
