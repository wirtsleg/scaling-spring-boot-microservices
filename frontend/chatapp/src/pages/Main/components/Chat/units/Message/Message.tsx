import dayjs from 'dayjs';

import { useUserContext } from 'providers';
import { Avatar } from 'components';
import { useMainPageContext } from 'pages/Main/hooks';

import { ChatMessage } from '../../types';

import {
  StyledBox,
  StyledDate,
  StyledMessage,
  StyledText,
} from './Message.styled';

export const Message = ({ text, authorId, createdAt }: ChatMessage) => {
  const { id, name } = useUserContext();
  const { activeContact } = useMainPageContext();
  const isUserMessage = authorId === id;
  const date = dayjs(createdAt).format('DD.MM.YYYY HH:mm:ss');

  return (
    <StyledBox {...{ isUserMessage }}>
      <StyledMessage {...{ isUserMessage }}>
        <StyledDate>{date}</StyledDate>
        <StyledText>{text}</StyledText>
      </StyledMessage>
      <Avatar size="md" name={isUserMessage ? name : activeContact!.name} />
    </StyledBox>
  );
};
