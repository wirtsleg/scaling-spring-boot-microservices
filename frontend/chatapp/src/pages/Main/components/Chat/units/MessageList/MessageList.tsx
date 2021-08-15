import { useEffect, useRef, useState } from 'react';

import { useStompClientContext, useUserContext } from 'providers';
import { useMainPageContext } from 'pages/Main/hooks';

import { ChatMessage } from '../../types';
import { Message } from '../Message';

import { StyledMessageList } from './MessageList.styled';

export const MessageList = () => {
  const ref = useRef<HTMLDivElement>(null);
  const stompClient = useStompClientContext();
  const { id } = useUserContext();
  const { activeContact } = useMainPageContext();
  const [messages, setMessages] = useState<ChatMessage[]>([]);

  useEffect(() => {
    const ids = [id, activeContact!.id];

    ids.sort((a, b) => a - b);

    const subscription = stompClient.subscribe(
      `/topic/chat/${ids.join('_')}/messages`,
      msg => {
        const items = JSON.parse(msg.body);

        if (Array.isArray(items)) {
          setMessages(prevState => [...prevState, ...items]);
        }
      }
    );

    return () => {
      setMessages([]);
      subscription.unsubscribe();
    };
  }, [id, activeContact, stompClient]);

  useEffect(() => {
    const element = ref.current;
    if (element) element.scrollTop = element.scrollHeight;
  }, [messages]);

  return (
    <StyledMessageList {...{ ref }}>
      {messages.map(message => (
        <Message key={message.id} {...message} />
      ))}
    </StyledMessageList>
  );
};
