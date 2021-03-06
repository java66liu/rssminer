(ns rssminer.search
  (:use [clojure.tools.logging :only [info]]
        [rssminer.util :only [ignore-error]]
        [rssminer.config :only [rssminer-conf cache-control cfg]])
  (:import rssminer.search.Searcher))

(defonce searcher (atom nil))

(defn running? [] @searcher)

(defn close-global-index-writer! [& {:keys [optimize]}]
  (when (running?)
    (.close ^Searcher @searcher (= optimize true))
    (reset! searcher nil)))

(defn use-index-writer! [& {:keys [path]}]
  (close-global-index-writer!)
  (let [path (or path (cfg :index-path))
        start (fn [] (reset! searcher
                            (Searcher/initGlobalSearcher path (cfg :data-source))))]
    (close-global-index-writer!)       ; close previous searcher
    (info "using index path" path)
    (if (= path "RAM")
      (start)
      (.start (Thread. start)))))

(defn index-feed [id rss-id {:keys [author tags title summary]}]
  (when (running?)
    (.index ^Searcher @searcher id rss-id author title summary tags)))

(defn search* [term tags authors userid limit offset fs]
  (if (running?)
    {:body (.search ^Searcher @searcher term tags authors userid limit offset fs)
     :headers cache-control}
    {:status 200 :body []}))

;; (defn search-within-subs [term uid subids limit]
;;   {:body (.searchInSubIDs ^Searcher @searcher term uid subids limit)
;;    :headers cache-control})
