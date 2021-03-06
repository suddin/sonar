/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2011 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

package org.sonar.channel;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChannelDispatcher<OUTPUT> extends Channel<OUTPUT> {

  private static final Logger logger = LoggerFactory.getLogger(ChannelDispatcher.class);
  private final boolean failIfNoChannelToConsumeOneCharacter;

  @SuppressWarnings("rawtypes")
  private final Channel[] channels;

  @SuppressWarnings("rawtypes")
  public ChannelDispatcher(List<Channel> channels) {
    this(channels, false);
  }

  @SuppressWarnings("rawtypes")
  public ChannelDispatcher(Channel... channels) {
    this(Arrays.asList(channels), false);
  }

  @SuppressWarnings("rawtypes")
  public ChannelDispatcher(List<Channel> channels, boolean failIfNoChannelToConsumeOneCharacter) {
    this.channels = channels.toArray(new Channel[channels.size()]);
    this.failIfNoChannelToConsumeOneCharacter = failIfNoChannelToConsumeOneCharacter;
  }

  public boolean consume(CodeReader code, OUTPUT output) {
    int nextChar = code.peek();
    while (nextChar != -1) {
      boolean channelConsumed = false;
      for (Channel<OUTPUT> channel : channels) {
        if (channel.consume(code, output)) {
          channelConsumed = true;
          break;
        }
      }
      if ( !channelConsumed) {
        String message = "None of the channel has been able to handle character '" + (char) code.peek() + "' (decimal value " + code.peek()
            + ") at line " + code.getLinePosition() + ", column " + code.getColumnPosition();
        if (failIfNoChannelToConsumeOneCharacter) {
          throw new IllegalStateException(message);
        }
        logger.debug(message);
        code.pop();
      }
      nextChar = code.peek();
    }
    return true;
  }
}