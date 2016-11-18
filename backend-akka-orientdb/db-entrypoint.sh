#!/bin/sh

if [ ! -d "$ORIENT_DB" ]; then
  echo "Running DB init"
  for f in /docker-entrypoint-initdb.d/*; do
    case "$f" in
      *.sh)      echo "$0: running $f"; . "$f" ;;
      *.osql)    echo "$0: running $f"; /orientdb/bin/console.sh "$f"; echo ;;
      *)         echo "$0: ignoring $f" ;;
    esac
    echo
  done
fi

exec "$@"
