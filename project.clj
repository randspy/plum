(defproject plum "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :profiles {:dev {:dependencies [[speclj "3.0.2"]
                                  [speclj-notify-osd "0.0.2"]]
                   :plugins [[speclj "3.0.2"]]
                   :test-paths ["spec"]}}
  :eval-in-leiningen true)
