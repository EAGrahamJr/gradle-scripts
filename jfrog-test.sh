#!/usr/bin/env sh
# A script to set up JFrog Artifactory OSS as a Docker container locally

JFROG=$(dirname $0)
[ "$JFROG" = "." ] && JFROG=$(pwd)
VAR_DIR="$JFROG/artifactory/var"

FIRST="false"
# set up the directory for first time use
# TODO tweak config?
if [ ! -d $VAR_DIR ]; then
  ETC_DIR="$VAR_DIR/etc"
  mkdir -p $ETC_DIR
  IP=$(hostname -I | cut -f 1 -d ' ')
  NAME="$(hostname).local"
  cat > $ETC_DIR/system.yaml << EOF
shared.node.id=${NAME}
shared.node.ip=${IP}
EOF
  chmod -R 777 $VAR_DIR
  FIRST="true"
fi

# run it
docker run --name artifactory \
  -v $VAR_DIR:/var/opt/jfrog/artifactory \
  -d -p 8081:8081 -p 8082:8082 \
  releases-docker.jfrog.io/jfrog/artifactory-oss:latest

echo "Artifactory is in the process of starting."
if [ $FIRST = "true" ]; then
  echo "First time setup may take a while..."
fi
