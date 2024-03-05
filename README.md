### Configuration
 * Change the configurations of Defects4J:
 ```shell
 $DEBUG=0 => $DEBUG=1 in DEFECTS4J_HOME/framework/core/Constant.pm
 ```

 * Change the configurations in `/resources/conf/system.properties` according to your system.


### Run
 * using the command `mvn package` for packaging, it will produce `PDA-1.0-SNAPSHOT-runnable.jar` in the folder of `artifacts`.
 * running with `PDA-1.0-SNAPSHOT-runnable.jar`
```shell
java -jar PDA-1.0-SNAPSHOT-runnable.jar trace [args] # call the function of program tracing with arguments [args]
java -jar PDA-1.0-SNAPSHOT-runnable.jar slice [args] # call the function of program slicing with arguments [args]
java -jar PDA-1.0-SNAPSHOT-runnable.jar dependency [args] # call the function of dependency builder with arguments [args]
```
