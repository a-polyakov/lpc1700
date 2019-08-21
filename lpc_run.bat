cd lpc1700
call mvn clean install
cd target
start lpc1700.jar
cd ../../master
call mvn clean install
cd target
start master.jar