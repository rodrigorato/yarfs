mkdir my-repo
pushd .
cd ../ca
mvn compile package
mvn install:install-file -Dfile=target/ca-1.0-SNAPSHOT.jar -DgroupId=a16 -DartifactId=ca -Dversion=1.0-SNAPSHOT -Dpackaging=jar -DlocalRepositoryPath=../client/my-repo
popd
mvn dependency:purge-local-repository
mvn -U compile exec:java -Dexec.args="https://server.yarfs:31001 --ca-addr=ca.yarfs"
