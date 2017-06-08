(ns ^:figwheel-no-load reagent-extra-methods-in-create-class-html.dev
  (:require [reagent-extra-methods-in-create-class-html.core :as core]
            [figwheel.client :as figwheel :include-macros true]))

(enable-console-print!)

(figwheel/watch-and-reload
  :websocket-url "ws://localhost:3449/figwheel-ws"
  :jsload-callback core/mount-root)

(core/init!)
