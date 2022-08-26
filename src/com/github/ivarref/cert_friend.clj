(ns com.github.ivarref.cert-friend
  (:import (java.util.concurrent TimeUnit)
           (okhttp3.tls HeldCertificate HeldCertificate$Builder)))

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
          (str (.certificatePem rootCertificate)
               (.certificatePem serverCertificate)
               (.privateKeyPkcs8Pem serverCertificate)))
    (println "Wrote server.keys")

    (spit "client.keys"
          (str (.certificatePem rootCertificate)
               (.certificatePem client)
               (.privateKeyPkcs8Pem client)))
    (println "Wrote client.keys")))
