package io.github.nanamochi.chio.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
  private String sender;
  private String content;
  private String target;
  private int senderId;

  public boolean isDirectMessage() {
    return !target.startsWith("#");
  }

  // Convert modern [url text] to legacy (text)[url]
  public String getContentMarkdownFormatted() {
    return content.replaceAll("\\[(https?://[^]]+)\\s+([^]]+)]", "($2)[$1]");
  }
}
