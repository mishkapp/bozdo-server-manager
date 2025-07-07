(ns bozdo-server-manager.telegram
  (:require [bozdo-server-manager.fields :as fields]
            [bozdo-server-manager.status :as status]
            [clojure.tools.logging :as log]
            [telegrambot-lib.core :as tbot]
            [config.core :refer [env]])
  (:use [bozdo-server-manager.database]))

(def superuser-id (keyword (str (:tg/superuser-id env))))

(def hotdog-reaction {:reaction [
                                 {:type  "emoji"
                                  :emoji "🌭"}
                                 ]
                      :is_big   true})

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

(defn handle-user [bot msg])

(defn handle-superuser [bot msg]
  (let [msg-text (-> msg
                         :message
                         :text)
        chat-id (-> msg
                    :message
                    :chat
                    :id)]
    (do
      (db-assoc-in "tg" ["msgs"] msg-text)
      (tbot/send-message bot chat-id msg-text))))

(defn handle-msg
  "Check the message text for command or string matches and handle the
   message appropriately."
  [bot msg]
  (log/debug "msg received:" msg)

  (let [user-id (-> msg
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

      (= superuser-id
         (keyword (str user-id))) (handle-superuser bot msg)

      :else (tbot/set-message-reaction bot chat-id message-id hotdog-reaction))
    ))

(defn app
  "Retrieve and process chat messages."
  [bot]

  (log/info "TG bot started started.")

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

(defn init
  []
  (log/info "Starting TG bot")
  (let [bot (tbot/create (:tg/bot-token env))
        me (tbot/get-me bot)]

    (if (:ok me)
      (do
        (log/info "Bot started successfully: " me)
        (app bot))
      (log/error "Required bot-token not found! Bot not started"))))