# yarfs
Yet another remote file service. Project for the Network and Computer Security course at IST Alameda 17/18.

## HTTPS

### Server
To enable HTTPS on the yarfs server, a valid certificate is needed.
If you want to create one yourself, use the included script (ca/generate.sh) to generate these certificates:
 1. root CA certificate
 2. CA certificate signed by the root CA
 3. server certificate signed by the CA

The root CA certificate can then be imported into your favourite browser for testing.

Here's how. Navigate to the ca folder:
`cd ca`
Then:
 1. generate the root CA certificate:
 `bash generate.sh root`

 2. generate the CA certificate signed by the root CA:
 `bash generate.sh ca`

 3. generate the server certificate signed by the CA:
 `bash generate.sh server`

When this is done, import the root CA (yarfs-root.pem) into your favourite browser and copy the yarfs-server.jks
keystore to the server directory. You should now be able to test HTTPS support on the server using your browser.

Note that the script assumes the root CA, CA and server use the domain names root.yarfs, ca.yarfs and server.yarfs,
respectively. You should either change or register them on you local hosts file.

### Client
To enable HTTPS on the yarfs client, simply run it using https:// in the base URL argument (use --help).
If using a local root CA (e.g. yarfs-root.pem), you must add it to the local list of trusted CAs.
Once again, navigate to the ca folder, `cd ca`, then:

 1. Convert the yarfs-root CA certificate to DER format:
 ` bash generate.sh root-to-der`

 2. Verify the root CA information. *Make sure it is correct*:
 `keytool -v -printcert -file yarfs-root.der`

 3. Find out where the keystore for the trusted CAs is located in your system. It is probably located in the following file, owned by root:
 `export CACERTS=$JAVA_HOME/jre/lib/security/cacerts`

 4. Create a backup of the trusted CA certificates keystore:
 `cp -v $CACERTS ~/$(basename $CACERTS).bak`

 5. As root, add our local root CA to the truted certificates keystore. The default password is _changeit_:
 `keytool -importcert -alias yarfs-root -keystore $CACERTS -file yarfs-root.der`

The client should now be able to use HTTPS. Test it using
 `cd client; mvn compile exec:java -Dexec.args="https://server.yarfs:31000"`

When you are done, restore the trusted certificates keystore ;) :
 `cp -v ~/$(basename $CACERTS).bak $CACERTS`


====== HOW TO RUN ======

1. First of all, this project uses the /var/yarfs directory to store all it's files, so whenever a user wants to upload/download files to/from the server, that's where they should/will be.
		- sudo mkdir -p /var/yarfs/server/files
		- sudo mkdir -p /var/yarfs/client/files
		- sudo mkdir -p /var/yarfs/client/.keys
		- sudo chown -R $USER:$USER /var/yarfs

2. Generate Certificates and Key Stores with our script.
	- Use password="password"

3. To use these certificates register `server.yarfs` and `ca.yarfs` in your /etc/hosts file (with the desired IP address - to run locally, 127.0.0.1 will do).

4. Add the Root CA certificate to your local Java list of trusted CAs as described in the README.md file.
	- Find it with `find / | grep -i cacert`
	- Mine (Arch Linux) was in /etc/ssl/certs/java/cacerts

5. Link/Copy the Server's Key Store in the server module's root directory ($YARFSROOT/server/).
	- like so: `ln -s ../root-ca/yarfs-server.jks .`

6. You can now run the server like:
	- `mvn clean compile exec:java -Dexec.args="31000 --ssl --listen=server.yarfs"`

7. Link/Copy the CA's Key Store in the CA module's root directory ($YARFSROOT/ca/).
	- like so: `ln -s ../root-ca/yarfs-ca.jks .`

8. You can now run the CA like:
	- `mvn clean compile exec:java -Dexec.args="https://server.yarfs:31000 --port=31001 --listen=ca.yarfs"`

9. Link/Copy the CA's certificate into the client module's root directory (~yarfsroot~/client/).
	-like so: `ln -s ../root-ca/yarfs-ca.pem .`

10. Finally, to run the client, you can use the runclient.sh we included, as this module depends on the CA module and the compilation process is troublesome.


====== EXAMPLE USAGE ======

0. Check the available commands with:
	- help

1. Run a client and then:
	- register mm ThisIsPassword

2. Run another client and then:
	- register pa HackWall

	




