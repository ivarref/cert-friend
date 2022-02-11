#!/bin/bash

set -ex

clojure -Sdeps '{:deps {less-awful-ssl/less-awful-ssl {:mvn/version "1.0.7-SNAPSHOT"}}}' \
        -M --report stderr \
        -e '(use (quote less.awful.ssl))
            (test-ssl "client.pkcs8" "client.crt" "server.pkcs8" "server.crt" "root.crt")
            (shutdown-agents)'

#clojure -Sdeps '{:deps {less-awful-ssl/less-awful-ssl {:mvn/version "1.0.7-SNAPSHOT"}}}' \
#        -M --report stderr \
#        -e '(use (quote less.awful.ssl))
#            (test-ssl "server.pkcs8" "server.crt" "server.pkcs8" "server.crt" "root.crt")
#            (shutdown-agents)'