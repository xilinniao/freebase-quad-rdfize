# Check out the code

# mvn clean install

# cd to project's root directory

# ./bin/setupRdfizer.sh (make sure you have write permission to / directory)

# cd freebaseRDFizer

# mvn exec:java -Dexec.mainClass="com.sj.freebase.data.rdf.DemoFreebaseRdfizer" -Dexec.args="<quad dump tsv file> <converted rdf file name>"