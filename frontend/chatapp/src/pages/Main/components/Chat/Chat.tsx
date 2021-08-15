import { useMainPageContext } from 'pages/Main/hooks';

import { MessageInput, MessageList } from './units';
import { StyledChat, StyledEmpty } from './Chat.styled';

export const Chat = () => {
  const { activeContact } = useMainPageContext();

  if (!activeContact)
    return <StyledEmpty>Select a chat to start messaging</StyledEmpty>;

  return (
    <StyledChat>
      <MessageList />
      <MessageInput />
    </StyledChat>
  );
};
