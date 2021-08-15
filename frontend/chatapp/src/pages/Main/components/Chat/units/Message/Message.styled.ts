import styled from 'styled-components';

export const StyledBox = styled.div<{ isUserMessage: boolean }>`
  display: flex;
  flex-direction: ${({ isUserMessage }) =>
    isUserMessage ? 'row' : 'row-reverse'};
  align-items: flex-end;
  align-self: ${({ isUserMessage }) =>
    isUserMessage ? 'flex-end' : 'flex-start'};
  max-width: 70%;
  margin-top: 10px;

  &:first-of-type {
    margin-top: 0;
  }
`;

export const StyledMessage = styled.div<{ isUserMessage: boolean }>`
  margin: ${({ isUserMessage }) =>
    `0 ${isUserMessage ? 10 : 0}px 0 ${isUserMessage ? 0 : 10}px`};
  padding: 10px;
  background: ${({ theme, isUserMessage }) =>
    isUserMessage ? theme.colors.dark : theme.colors.primary};
  border-radius: 5px;
`;

export const StyledDate = styled.div`
  font-size: 12px;
  opacity: 0.7;
`;

export const StyledText = styled.div`
  margin-top: 10px;
`;
