#! /bin/bash

# Generates a trust chain using a root CA, CA and server.
# The server certificate is signed by the CA, whose
# certificate is signed by the root CA, creating the following trust chain:
#   root CA <- CA <- server

ROOT_DOMAIN="root.yarfs"
CA_DOMAIN="ca.yarfs"
SERVER_DOMAIN="server.yarfs"

STOREPASS="password" # FIXME this should not be hardcoded in production
keysize=4096
keytype="-keyalg RSA -keysize $keysize"

storetype="-storetype pkcs12"

# FIXME: for now, assuming the root CA, CA and server are in the same machine

test_error() {
	if test $1 -ne 0; then
		echo "error($1): $@"
		exit 1
	fi
}

gen_root() {
	echo "--> Generating root CA keypair"
	keytool -v -genkeypair $keytype $storetype -keystore yarfs-root.jks -alias root -ext bc:c \
		-dname "CN=${ROOT_DOMAIN}, OU=SIRS, O=IST, L=Lisbon, S=Lisbon, C=PT"
	#	-ext SubjectAlternativeName:critical="${ROOT_DOMAIN}"
	test_error $?

	echo "--> Exporting root CA key"
	keytool $storetype -keystore yarfs-root.jks -alias root -exportcert -rfc > yarfs-root.pem
	test_error $?
}

# imports <cert> as a trusted ca into <keystore>
# args: <keystore> <cert>
import_trustcert() {
	echo "--> Adding trusted CA '$2' to '$1'"
	keytool -v $storetype -keystore $1 -importcert -trustcacerts -file $2
	test_error $?
}

gen_ca() {
	echo "--> Generating CA keypair"
	keytool -v -genkeypair $keytype $storetype -keystore yarfs-ca.jks -alias ca -ext bc:c \
		-dname "CN=${CA_DOMAIN}, OU=SIRS, O=IST, L=Lisbon, S=Lisbon, C=PT"
	test_error $?

	import_trustcert yarfs-ca.jks yarfs-root.pem
	
	echo "--> Creating a certificate request and signing it using the root CA"
	keytool -storepass $STOREPASS $storetype -keystore yarfs-ca.jks -certreq -alias ca \
		| keytool -storepass $STOREPASS $storetype -keystore yarfs-root.jks \
			-gencert -alias root -ext BC=0 -rfc > yarfs-ca.pem
	test_error $?

	echo "--> Importing the signed cert to yarfs-ca.jks"
	keytool -v $storetype -keystore yarfs-ca.jks -importcert -alias ca -file yarfs-ca.pem
	test_error $?
}

gen_server() {
	echo "--> Generating server cert"
	keytool -v -genkeypair $keytype $storetype -keystore yarfs-server.jks -alias server \
		-ext SubjectAlternativeName:critical=DNS:${SERVER_DOMAIN} \
		-dname "CN=${SERVER_DOMAIN}, OU=SIRS, O=IST, L=Lisbon, S=Lisbon, C=PT"
	test_error $?

	import_trustcert yarfs-server.jks yarfs-ca.pem

	echo "--> Creating a certificate request and signing it using the CA"
	keytool -storepass $STOREPASS $storetype -keystore yarfs-server.jks -certreq -alias server \
		| tee yarfs-server.req \
		| keytool -storepass $STOREPASS $storetype -keystore yarfs-ca.jks -gencert -alias ca \
			-ext KeyUsage:c=digitalSignature,keyEncipherment \
			-ext SubjectAlternativeName:critical=DNS:${SERVER_DOMAIN} \
			-rfc > yarfs-server.pem
	test_error $?

	echo "--> Importing the signed cert to yarfs-server.jks"
	keytool -v $storetype -keystore yarfs-server.jks -importcert -alias server -file yarfs-server.pem
	test_error $?
}
#	cat yarfs-root.pem yarfs-ca.pem yarfs-server.pem |
#		keytool $storetype -keystore yarfs-server.jks -importcert -alias server

# args: file.pem
fingerprint() {
	echo "=== $1 ==="
	openssl x509 -noout -fingerprint -sha256 -inform pem -in $1
}

# args: file.pem
# will create file.pub
export_pub() {
	out=$(basename $1).pub
	echo "=== $1 -> $out ==="
	openssl x509 -in $1 -noout -pubkey > $out
}

if test -z "$1"; then
	echo "tell me what key/certs to generate: root, ca or server"
	exit 1
fi

if test $1 = root; then
	gen_root
elif test $1 = "root-to-der"; then
	openssl x509 -in yarfs-root.pem -inform pem -out yarfs-root.der -outform der
elif test $1 = ca; then
	gen_ca
elif test $1 = server; then
	gen_server
elif test $1 = fingerprints || test $1 = fp; then
	for f in *.pem; do
		fingerprint $f
		echo
	done
elif test $1 = "export-pub" || test $1 = ep; then
	for f in *.pem; do
		export_pub $f
		echo -n "base64(sha256(pub)) = 	"
		cat $(basename $f).pub | openssl dgst -sha256 -binary | openssl enc -base64
		echo
	done
else
	echo unknown action
fi
