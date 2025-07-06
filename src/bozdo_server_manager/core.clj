(ns bozdo-server-manager.core
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]
            [telegrambot-lib.core :as tbot]
            [bozdo-server-manager.fields :as fields]
            [bozdo-server-manager.status :as status]
            ))

(defonce users
         (atom {
                :288220112 {
                            :username "mishkapp"
                            :is-admin true
                            }

                :416993603 {
                            :username "Prooty"
                            :is-admin true
                            }
                }))

(defn create-bot
  "Create a Telegram bot instance."
  ([]
   (tbot/create))
  ([token]
   (tbot/create token)))

(defn poll-updates
  "Long poll for recent chat messages from Telegram."
  ([bot]
   (poll-updates bot nil))

  ([bot offset]
   (try
     (tbot/get-updates bot {:offset  offset
                            :timeout 10})

     (catch Exception e
       (log/error "tbot/get-updates exception:" e)))))

(defn handle-msg
  "Check the message text for command or string matches and handle the
   message appropriately."
  [bot msg]
  (log/info "msg received:" msg)

  (let [msg-text (fields/text msg)
        user-id (-> msg
                    :message
                    :from
                    :id)
        chat-id (-> msg
                    :message
                    :chat
                    :id)
        message-id (-> msg
                       :message
                       :message_id)]

    (cond
      (contains? @users (keyword (str user-id))) (tbot/send-message bot chat-id msg-text)
      :else (tbot/set-message-reaction bot chat-id message-id {:reaction [
                                                                          {:type  "emoji"
                                                                           :emoji "🌭"}
                                                                          ]
                                                               :is_big   true}))
    ))

(defn app
  "Retrieve and process chat messages."
  [bot]
  (log/info "lemme-know-bot service started.")
  (tbot/set-my-commands bot [{:command     "help"
                                    :description ""}])

  (loop []
    (log/debug "checking for chat updates.")
    (let [updates (poll-updates bot @status/update-id)
          messages (fields/chat-results updates)]

      ;; Check all messages, if any, for commands/keywords.
      (doseq [msg messages]
        (handle-msg bot msg)
        ;; Increment the next update-id to process.
        (-> msg
            (fields/update-id)
            (inc)
            (status/set-id!)))

      ;; Wait a while before checking for updates again.
      (Thread/sleep 1))
    (recur)))

;(defn shutdown-service
;  "Shutdown the service cleanly."
;  []
;  (shutdown-agents)
;
;  (when (seq @notify/searches)
;    (log/info "saving searches list to:" (:searches cfg/config))
;    (cfg/save-file (into [] @notify/searches) (:searches cfg/config)))
;
;  (log/info "lemme-know-bot service exited."))

(defn -main
  "Create the Telegram bot and run the application."
  []
  (log/info "starting lemme-know-bot service.")

  ;(.addShutdownHook (Runtime/getRuntime)
  ;                  (Thread. ^Runnable shutdown-service))

  (let [bozdo-bot (create-bot (System/getenv "TG_BOT_TOKEN"))]

    (if (some? (:bot-token bozdo-bot))
      (app bozdo-bot)
      (log/error "required bot-token not found! exiting."))))