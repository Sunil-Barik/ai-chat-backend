package util;


import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * SseEmitter that buffers every chunk sent to it so the full AI reply
 * can be persisted to the database once streaming completes.
 */
public class ChatSseEmitter extends SseEmitter {

    private final StringBuilder buffer = new StringBuilder();

    public ChatSseEmitter(Long timeout) {
        super(timeout);
    }

    /**
     * Use this instead of emitter.send(SseEmitter.event().data(chunk))
     * so the chunk gets captured as well as streamed to the client.
     */
    public void sendChunk(String chunk) throws IOException {
        if (chunk != null) {
            buffer.append(chunk);
        }
        super.send(SseEmitter.event().data(chunk));
    }

    public String getFullText() {
        return buffer.toString();
    }
}