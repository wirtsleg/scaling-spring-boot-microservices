import { ChangeEvent, KeyboardEvent, useState } from 'react';

import { useStompClientContext } from 'providers';
import { useMainPageContext } from 'pages/Main/hooks';

import {
  StyledBox,
  StyledInput,
  StyledSendButton,
} from './MessageInput.styled';

export const MessageInput = () => {
  const stompClient = useStompClientContext();
  const { activeContact } = useMainPageContext();
  const [value, setValue] = useState('');

  const sendMessage = () => {
    if (value.length > 0) {
      stompClient.publish({
        destination: `/app/chat/${activeContact!.id}/messages/add`,
        body: value,
      });
      setValue('');
    }
  };

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    setValue(e.target.value);
  };

  const handleKeyPress = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') sendMessage();
  };

  return (
    <StyledBox>
      <StyledInput
        {...{ value }}
        onChange={handleChange}
        onKeyPress={handleKeyPress}
      />
      <StyledSendButton onClick={sendMessage}>Send</StyledSendButton>
    </StyledBox>
  );
};
