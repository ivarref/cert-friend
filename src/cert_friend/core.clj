(ns cert-friend.core
  (:require [clojure.string :as str])
  (:import (java.security KeyPairGenerator)
           (java.util.concurrent TimeUnit)
           (okhttp3.tls HeldCertificate HeldCertificate$Builder)))


(defn named-cert [nam cert-str]
  (-> cert-str
      (str/replace "-----BEGIN CERTIFICATE-----" (str "-----BEGIN " nam " CERTIFICATE-----"))
      (str/replace "-----END CERTIFICATE-----" (str "-----END " nam " CERTIFICATE-----"))))


(defn root-ca []
  (let [^KeyPairGenerator kpg (doto (KeyPairGenerator/getInstance "RSA")
                                (.initialize 2048))
        ^HeldCertificate rootCertificate (-> (HeldCertificate$Builder.)
                                             (.certificateAuthority 0)
                                             (.duration (* 1 365) TimeUnit/DAYS)
                                             ;(.keyPair (.generateKeyPair kpg))
                                             (.build))
        ^HeldCertificate serverCertificate (-> (HeldCertificate$Builder.)
                                               (.signedBy rootCertificate)
                                               (.duration (* 1 365) TimeUnit/DAYS)
                                               ;(.keyPair (.generateKeyPair kpg))
                                               (.build))
        ^HeldCertificate client (-> (HeldCertificate$Builder.)
                                    (.signedBy rootCertificate)
                                    (.duration (* 1 365) TimeUnit/DAYS)
                                    ;(.keyPair (.generateKeyPair kpg))
                                    (.build))]
    (spit "server.keys"
          (str (named-cert "ROOT" (.certificatePem rootCertificate))
               (named-cert "SELF" (.certificatePem serverCertificate))
               (.privateKeyPkcs8Pem serverCertificate)))

    (spit "client.keys"
          (str (named-cert "ROOT" (.certificatePem rootCertificate))
               (named-cert "SELF" (.certificatePem client))
               (.privateKeyPkcs8Pem client)))

    (spit "root.crt" (.certificatePem rootCertificate))

    (spit "server.crt" (.certificatePem serverCertificate))
    (spit "server.pkcs8" (.privateKeyPkcs8Pem serverCertificate))

    (spit "client.crt" (.certificatePem client))
    (spit "client.pkcs8" (.privateKeyPkcs8Pem client))))
