package ru.netology

data class User(val id: Int)

data class Message(
    val id: Int,
    val sender: User,
    val recipient: User,
    var content: String,
    var isRead: Boolean = false
)

data class Chat(
    val id: Int,
    val user1: User,
    val user2: User,
    val messages: MutableList<Message>,
    var unreadCount: Int = 0
)

class ChatService {
    val chats: MutableMap<Int, Chat> = mutableMapOf()
    private var nextMessageId = 0


    private fun createChat(id: Int, user1: User, user2: User): Chat {
        val chat = Chat(id, user1, user2, mutableListOf())
        chats[id] = chat
        return chat
    }

    fun getChats(userId: Int): List<Chat> {
        return chats.values.filter { it.user1.id == userId || it.user2.id == userId }
    }

    fun getUnreadChatsCount(userId: Int): Int {
        return getChats(userId).count { it.unreadCount > 0 }
    }

    fun getMessages(chatId: Int, recipientId: Int, count: Int): List<Message> {
        val chat = chats[chatId] ?: return emptyList()
        chat.messages
            .asSequence()
            .filter { !it.isRead && it.recipient.id == recipientId }
            .onEach { it.isRead = true }
            .toList()
        chat.unreadCount = chat.messages.count { !it.isRead && it.recipient.id == recipientId }
        return chat.messages.takeLast(count)
    }

    fun getLastMessages(userId: Int): List<String> {
        return getChats(userId)
            .asSequence()
            .mapNotNull { chat ->
                chat.messages.lastOrNull()?.content ?: "нет сообщений"
            }
            .toList()
    }

    fun sendMessage(chatId: Int, sender: User, recipient: User, messageContent: String): Chat {
        val chat = chats[chatId] ?: createChat(chatId, sender, recipient)
        val message = Message(
            ++nextMessageId,
            sender,
            recipient,
            messageContent
        )
        chat.messages.add(message)
        chat.unreadCount++
        return chat
    }

    fun editMessage(messageId: Int, newContent: String): Boolean {
        return chats.values.any { chat ->
            chat.messages.any { message ->
                if (message.id == messageId) {
                    message.content = newContent
                    true
                } else {
                    false
                }
            }
        }
    }

    fun deleteMessage(messageId: Int): Boolean {
        return chats.values.any { chat ->
            chat.messages.removeAll { it.id == messageId }
        }
    }

    fun deleteChat(chatId: Int): Boolean {
        return chats.remove(chatId) != null
    }
}

fun main() {
}