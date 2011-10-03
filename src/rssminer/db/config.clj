(ns rssminer.db.config
  (:use [rssminer.database :only [h2-db-factory]]
        [rssminer.util :only [ignore-error]]
        [rssminer.db.util :only [h2-query]]
        [clojure.java.jdbc :only [with-connection insert-record]]))

(defn fetch-black-domain-pattens []
  (map #(re-pattern (:patten %))
       (h2-query ["select patten from black_domain_pattens"])))

(defn insert-black-domain-patten [patten]
  (ignore-error ;;ignore voilate of uniqe constraint
   (with-connection @h2-db-factory
     (insert-record :black_domain_pattens
                    {:patten patten}))))

(defn fetch-multi-domains []
  (map :domain
       (h2-query ["SELECT * FROM multi_rss_domains"])))

