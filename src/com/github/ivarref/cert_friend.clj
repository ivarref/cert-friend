(ns com.github.ivarref.cert-friend
  (:require [clojure.string :as str])
  (:import (java.util.concurrent TimeUnit)
           (okhttp3.tls HeldCertificate HeldCertificate$Builder)))

(defn named-cert [nam cert-str]
  (-> cert-str
      (str/replace "-----BEGIN CERTIFICATE-----" (str "-----BEGIN " nam " CERTIFICATE-----"))
      (str/replace "-----END CERTIFICATE-----" (str "-----END " nam " CERTIFICATE-----"))))

(defn write-certs [{:keys [duration]
                    :or   {duration 365}}]
  (let [^HeldCertificate rootCertificate (-> (HeldCertificate$Builder.)
                                             (.certificateAuthority 0)
                                             (.duration duration TimeUnit/DAYS)
                                             (.build))
        ^HeldCertificate serverCertificate (-> (HeldCertificate$Builder.)
                                               (.signedBy rootCertificate)
                                               (.duration duration TimeUnit/DAYS)
                                               (.build))
        ^HeldCertificate client (-> (HeldCertificate$Builder.)
                                    (.signedBy rootCertificate)
                                    (.duration duration TimeUnit/DAYS)
                                    (.build))]
    (spit "server.keys"
          (str (named-cert "ROOT" (.certificatePem rootCertificate))
               (named-cert "SELF" (.certificatePem serverCertificate))
               (.privateKeyPkcs8Pem serverCertificate)))

    (spit "client.keys"
          (str (named-cert "ROOT" (.certificatePem rootCertificate))
               (named-cert "SELF" (.certificatePem client))
               (.privateKeyPkcs8Pem client)))))

