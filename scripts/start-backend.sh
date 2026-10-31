#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

if [[ -n "${DATABASE_URL:-}" && -z "${SPRING_DATASOURCE_URL:-}" ]]; then
  eval "$(python3 - <<'PY'
import os
import shlex
from urllib.parse import urlsplit
raw = os.environ['DATABASE_URL']
parsed = urlsplit(raw)
query = 'useSSL=true&verifyServerCertificate=false&serverTimezone=UTC'
jdbc = f'jdbc:mysql://{parsed.hostname}:{parsed.port or 3306}{parsed.path}?{query}'
print('export SPRING_DATASOURCE_URL=' + shlex.quote(jdbc))
if parsed.username:
    print('export SPRING_DATASOURCE_USERNAME=' + shlex.quote(parsed.username))
if parsed.password:
    print('export SPRING_DATASOURCE_PASSWORD=' + shlex.quote(parsed.password))
PY
)"
fi

cd "$ROOT_DIR"
exec mvn -f libraryapp/pom.xml spring-boot:run
