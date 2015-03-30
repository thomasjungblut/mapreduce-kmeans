# mapreduce-kmeans

Code for [this blobpost](http://codingwiththomas.blogspot.com/2011/05/k-means-clustering-with-mapreduce.html).

Please note that this is an *example* and not production-ready code. If you want a productionalized and working clustering, use Mahout, Hama or Spark.

Build
-----

You will need Java 8 to build this library.

You can simply build with:
 
> mvn clean package install

The created jars contains debuggable code + sources + javadocs.


To generate eclipse files use:

> mvn eclipse:eclipse
